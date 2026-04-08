package com.linyi.pig.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    private final MinioProperties properties;

    public MinioConfiguration(MinioProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    public String getBucketName() {
        return properties.getBucketName();
    }

    public String getUrl() {
        return properties.getUrl();
    }
}
