package com.vsp.accidentManagement.utilities;

import com.cloudinary.Cloudinary;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryUpload {

//    @Value("${cloudinary.cloud-name}")
    private final String cloudName="dnw1zofen";

//    @Value("${cloudinary.api-key}")
    private final String apiKey="874751766555975";

//    @Value("${cloudinary.api-secret}")
    private final String apiSecret="7yzAoNj_ewEirBiVi5b7P0ilmAg";

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}