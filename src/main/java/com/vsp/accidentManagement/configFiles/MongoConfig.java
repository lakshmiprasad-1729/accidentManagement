package com.vsp.accidentManagement.configFiles;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // This enables MongoDB auditing which makes @CreatedDate and @LastModifiedDate work
}