package com.ddsolutions.rsvp.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSClientConfiguration {
    private static AWSCredentialsProvider awsCredentials;
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSClientConfiguration.class);

    @Value("${isRunningInEC2}")
    private boolean isRunningInEC2;

    @Value("${isRunningInLocal}")
    private boolean isRunningInLocal;

    @Bean
    public AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getAwsCredentials())
                .withRegion(Regions.US_EAST_1).build();
    }

    private AWSCredentialsProvider getAwsCredentials() {
        if (awsCredentials == null) {
            if (isRunningInEC2) {
                awsCredentials = new InstanceProfileCredentialsProvider(true);
            } else if (isRunningInLocal) {
                awsCredentials = new ProfileCredentialsProvider("admin");
            } else {
                awsCredentials = new DefaultAWSCredentialsProviderChain();
            }
        }
        return awsCredentials;
    }
}
