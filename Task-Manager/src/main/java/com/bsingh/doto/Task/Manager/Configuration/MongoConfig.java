package com.bsingh.doto.Task.Manager.Configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb+srv://sablolhai2003:hpGyd1j7YHklgsy4@cluster0.blz4ijo.mongodb.net/todo_manager?retryWrites=true&w=majority&appName=Cluster0");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "todo_manager");
    }
}
