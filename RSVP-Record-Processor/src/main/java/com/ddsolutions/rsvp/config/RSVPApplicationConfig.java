package com.ddsolutions.rsvp.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.ddsolutions.rsvp.kinesis.EventProcessorFactory;
import com.ddsolutions.rsvp.kinesis.KinesisRecordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.leases.LeaseManagementConfig;
import software.amazon.kinesis.metrics.MetricsConfig;
import software.amazon.kinesis.metrics.MetricsLevel;
import software.amazon.kinesis.processor.ProcessorConfig;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class RSVPApplicationConfig {

    private ApplicationContext applicationContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(RSVPApplicationConfig.class);

    @Value("${stream.name}")
    private String streamName;

    @Value("${app.name}")
    private String appName;

    @Value("${table.name}")
    private String tableName;

    @Autowired
    public RSVPApplicationConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Supplier<KinesisRecordProcessor> createProcessor() {
        return () -> applicationContext.getBean(KinesisRecordProcessor.class);
    }

    @Bean
    public KinesisAsyncClient createKinesisClient() {
        return KinesisAsyncClient.builder()
                .credentialsProvider(new AwsCredentialsProvider() {
                    @Override
                    public AwsCredentials resolveCredentials() {
//                        return InstanceProfileCredentialsProvider.create().resolveCredentials();
                        return ProfileCredentialsProvider.create("doubledigit").resolveCredentials();
                    }
                }).region(Region.US_EAST_1).build();
    }

    @Bean
    public DynamoDbAsyncClient createDynamoDBClient() {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(new AwsCredentialsProvider() {
                    @Override
                    public AwsCredentials resolveCredentials() {
//                        return InstanceProfileCredentialsProvider.create().resolveCredentials();
                        return ProfileCredentialsProvider.create("doubledigit").resolveCredentials();
                    }
                }).region(Region.US_EAST_1).build();
    }

    @Bean
    public CloudWatchAsyncClient createCloudWatchClient() {
        return CloudWatchAsyncClient.builder()
                .credentialsProvider(new AwsCredentialsProvider() {
                    @Override
                    public AwsCredentials resolveCredentials() {
//                        return InstanceProfileCredentialsProvider.create().resolveCredentials();
                        return ProfileCredentialsProvider.create("doubledigit").resolveCredentials();
                    }
                }).region(Region.US_EAST_1).build();
    }

    @Bean
    public ConfigsBuilder createConfigBuilder(KinesisAsyncClient kinesisAsyncClient,
                                              DynamoDbAsyncClient dynamoDbAsyncClient,
                                              CloudWatchAsyncClient cloudWatchAsyncClient) {
        return new ConfigsBuilder(streamName,
                appName,
                kinesisAsyncClient,
                dynamoDbAsyncClient,
                cloudWatchAsyncClient,
                UUID.randomUUID().toString(),
                new EventProcessorFactory(createProcessor()));
    }

    @Bean
    public Scheduler creatScheduler(ConfigsBuilder configsBuilder) {
        ProcessorConfig processorConfig = configsBuilder.processorConfig()
                .callProcessRecordsEvenForEmptyRecordList(true);
        MetricsConfig metricsConfig = configsBuilder.metricsConfig().metricsLevel(MetricsLevel.NONE);
        LeaseManagementConfig leaseManagementConfig =
                configsBuilder.leaseManagementConfig()
                        .cleanupLeasesUponShardCompletion(true)
                        .maxLeasesForWorker(25)
                        .consistentReads(false);

        return new Scheduler(configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                leaseManagementConfig,
                configsBuilder.lifecycleConfig(),
                metricsConfig,
                processorConfig,
                configsBuilder.retrievalConfig());
    }
}
