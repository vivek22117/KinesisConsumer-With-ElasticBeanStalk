package com.ddsolutions.rsvp.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ddsolutions.rsvp.utility.PropertyLoaderUtility.getInstance;
import static java.lang.Boolean.parseBoolean;

@Configuration
public class AWSClientConfiguration {
    private static AWSCredentials awsCredentials;
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSClientConfiguration.class);

    @Bean
    public AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard().withCredentials(new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return getAwsCredentials();
            }

            @Override
            public void refresh() {
                LOGGER.debug("Nothing required here....");
            }
        }).withRegion(Regions.US_EAST_1).build();
    }

    private static AWSCredentials getAwsCredentials() {
        if (awsCredentials == null) {
            boolean isRunningInEC2 = parseBoolean(getInstance().getProperty("isRunningInEC2"));
            boolean isRunningInLocal = parseBoolean(getInstance().getProperty("isRunningInLocal"));
            if (isRunningInEC2) {
                awsCredentials = new InstanceProfileCredentialsProvider(true).getCredentials();
            } else if (isRunningInLocal) {
                awsCredentials = new ProfileCredentialsProvider("doubledigit").getCredentials();
            } else {
                awsCredentials = new DefaultAWSCredentialsProviderChain().getCredentials();
            }
        }
        return awsCredentials;
    }
}
