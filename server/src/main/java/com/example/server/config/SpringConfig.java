package com.example.server.config;

import com.example.server.util.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        AppProperties.class
})
public class SpringConfig {

}