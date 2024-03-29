# mq2elasticsearch项目结构
![](images/binlog-2.png)

# 主要代码
## 添加maven依赖
```xml
<properties>
    <java.version>1.8</java.version>
    <elasticsearch.version>7.2.0</elasticsearch.version>
</properties>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.62</version>
</dependency>
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.5.2</version>
</dependency>
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>${elasticsearch.version}</version>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-client</artifactId>
    <version>${elasticsearch.version}</version>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>${elasticsearch.version}</version>
</dependency>
```

## application.yml
```
spring:
  application:
    name: mq2elasticsearch
server:
  port: 8086

rocketmq:
  host: 192.168.0.2:9876
  tags: '*'
  consumer:
    group: binlog
    topic:
      student: test_student
      course: test_course

elasticsearch:
  host: 192.168.0.3:9200
  index:
    student: test_student
    course: test_course
```

## 把RocketMQ的数据添加到ES的批量列表
```java
@Service
public class StudentConsumerService implements ConsumerService {
    @Override
    public void execute(MessageExt msg, BulkRequest bulkRequest) throws Exception {
        String message = new String(msg.getBody(), charset);
        String index = msg.getTopic();
        BinlogDto binlog = JSON.parseObject(message, BinlogDto.class);
        JSONArray array = JSON.parseArray(binlog.getData().toString());
        String id = array.get(0).toString();
        if (binlog.getEvent().equals(EventType.INSERT.getCode())) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", array.get(0));
            map.put("name", array.get(1));
            IndexRequest request = new IndexRequest(index)
                    .id(id)
                    .source(map);
            bulkRequest.add(request);
        } else if (binlog.getEvent().equals(EventType.UPDATE.getCode())) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", array.get(1));
            UpdateRequest request = new UpdateRequest(index, id)
                    .doc(map);
            bulkRequest.add(request);
        } else if (binlog.getEvent().equals(EventType.DELETE.getCode())) {
            DeleteRequest request = new DeleteRequest(index, id);
            bulkRequest.add(request);
        }
    }
}
```

## RocketMQ消费消息
```java
@Component
@Slf4j
public class RocketMqListener implements MessageListenerConcurrently {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RocketMqConfig mqConfig;
    @Autowired
    private StudentConsumerService studentConsumerService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        BulkRequest bulkRequest = new BulkRequest();
        try {
            for (MessageExt message : list) {
                if (message.getTopic().equals(mqConfig.studentTopic)) {
                    studentConsumerService.execute(message, bulkRequest);
                } else if (message.getTopic().equals(mqConfig.courseTopic)) {

                }
            }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
```
