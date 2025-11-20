package br.org.apae.atendimento.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("presignedUrls");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(55, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats()
        );

        System.out.println("✅ CacheManager configurado com Caffeine");
        return cacheManager;
    }
}