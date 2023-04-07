package com.ksearch.back.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy;

@Configuration
public class ElasticsearchConfig {
    @Configuration
    public class ElasticsearchClientConfig extends AbstractElasticsearchConfiguration {

        @Value("${elasticsearch.host}")
        private String host;

        @Value("${elasticsearch.port}")
        private int port;

        @Override
        @Bean
        public RestHighLevelClient elasticsearchClient() {
            SnakeCaseFieldNamingStrategy a;
            final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(host + ":" + port)
//                    .usingSsl()        // connect to https
                    .build();

            return RestClients.create(clientConfiguration).rest();
        }

        @Bean
        public ElasticsearchOperations elasticsearchOperations() {
            return new ElasticsearchRestTemplate(elasticsearchClient());
        }
    }
}
