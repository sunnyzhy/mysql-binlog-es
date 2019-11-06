package com.zhy.mq2elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhy
 * @date 2019/11/6 14:44
 */
@Configuration
public class ElasticsearchConfig {
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.index.student}")
    public String studentIndex;
    @Value("${elasticsearch.index.course}")
    public String courseIndex;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        List<HttpHost> hostList = new ArrayList<>();
        for (String host : host.split(",")) {
            String[] hostInfo = host.split(":");
            if (hostInfo.length != 2) {
                continue;
            }
            HttpHost httpHost = new HttpHost(hostInfo[0],
                    Integer.parseInt(hostInfo[1]),
                    "http");
            hostList.add(httpHost);
        }
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient
                        .builder(hostList.toArray(new HttpHost[hostList.size()]))
                        .setRequestConfigCallback(requestConfigCallback -> {
                            requestConfigCallback.setConnectionRequestTimeout(5000);
                            requestConfigCallback.setSocketTimeout(30000);
                            return requestConfigCallback;
                        }));
        return client;
    }
}
