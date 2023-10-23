package it.gov.pagopa.miladapter.config;

import it.gov.pagopa.miladapter.model.Token;
import it.gov.pagopa.miladapter.properties.CacheConfigurationProperties;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Autowired
    CacheConfigurationProperties cacheConfigurationProperties;

    @Bean
    public CacheManager EhcacheManager() {
        return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(cacheConfigurationProperties.getCacheName(), CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, Token.class, ResourcePoolsBuilder.heap(cacheConfigurationProperties.getMaxEntries()))
                        .withExpiry(new CustomExpiryPolicy())
                )
                .build(true);
    }
}