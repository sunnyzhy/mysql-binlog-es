package com.zhy.binlog2mq.config;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhy
 * @date 2019/11/5 15:54
 */
@Configuration
public class BinglogConfig {
    @Value("${binglog.host}")
    private String host;
    @Value("${binglog.port}")
    private Integer port;
    @Value("${binglog.user}")
    private String user;
    @Value("${binglog.password}")
    private String password;
    @Value("${binglog.table}")
    private String table;

    @Bean
    public BinaryLogClient client() {
        BinaryLogClient client = new BinaryLogClient(host, port, user, password);
        return client;
    }

    @Bean
    public List<String> table() {
        String[] array = table.split(",");
        List<String> list = new ArrayList<>(array.length);
        Collections.addAll(list, array);
        List<String> table = list
                .stream()
                .map(x -> x.replace('.', '_'))
                .collect(Collectors.toList());
        return table;
    }
}
