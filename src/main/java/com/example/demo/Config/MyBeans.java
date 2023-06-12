package com.example.demo.Config;

import com.example.demo.JustClasses.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBeans
{
    @Bean
    public Jwt myBean() {
        return new Jwt();
    }
}
