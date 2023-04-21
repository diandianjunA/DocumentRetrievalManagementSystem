package com.project.documentretrievalmanagementsystem.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    @Value("${spring.elasticsearch.rest.host}")
    private String host;
    @Value("${spring.elasticsearch.rest.enable:true}")
    private boolean enable;

    @Value("${spring.elasticsearch.rest.port}")
    private int port;


    //注入IOC容器
    @Bean
    public ElasticsearchClient elasticsearchClient(){
        // Create the low-level client
        RestClient restClient = RestClient.builder(new HttpHost(host, port)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }
}
