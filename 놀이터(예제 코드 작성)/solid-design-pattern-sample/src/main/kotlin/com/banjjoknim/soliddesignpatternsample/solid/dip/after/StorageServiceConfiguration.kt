package com.banjjoknim.soliddesignpatternsample.solid.dip.after

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StorageServiceConfiguration {

    /**
     * AmazonStorageService 를 적용하고 싶은 경우
     */
    @Bean
    fun storageService(): StorageService {
        return AmazonStorageService()
    }

    /**
     * GoogleStorageService 를 적용하고 싶은 경우
     */
//    @Bean
//    fun storageService(): StorageService {
//        return GoogleStorageService()
//    }
}
