package com.jereczek.checkers.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CpuMovesAsyncConfig {

    @Bean
    public ExecutorService cpuMovesAsyncExecutorService() {
        return Executors.newCachedThreadPool();
    }
}
