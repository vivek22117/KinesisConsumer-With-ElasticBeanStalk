package com.ddsolutions.rsvp.config;

import com.ddsolutions.rsvp.kinesis.EventProcessorFactory;
import com.ddsolutions.rsvp.kinesis.KinesisRecordProcessor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
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
import software.amazon.kinesis.retrieval.RetrievalConfig;

import java.util.UUID;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;
import static software.amazon.kinesis.common.InitialPositionInStream.TRIM_HORIZON;
import static software.amazon.kinesis.common.InitialPositionInStreamExtended.newInitialPosition;

@Configuration
public class RSVPApplicationConfig {

    private ApplicationContext applicationContext;
    private static AwsCredentialsProvider awsCredentialsProvider;
    private static Logger logger = getLogger(RSVPApplicationConfig.class);

    @Value("${stream.name}")
    private String streamName;

    @Value("${app.name}")
    private String appName;

    @Value("${table.name}")
    private String tableName;

    @Value("${isRunningInEC2}")
    private boolean isRunningInEC2;

    @Value("${isRunningInLocal}")
    private boolean isRunningInLocal;

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
                .credentialsProvider(getAwsCredentials()).region(Region.US_EAST_1).build();
    }

    @Bean
    public DynamoDbAsyncClient createDynamoDBClient() {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(getAwsCredentials()).region(Region.US_EAST_1).build();
    }

    @Bean
    public CloudWatchAsyncClient createCloudWatchClient() {
        return CloudWatchAsyncClient.builder()
                .credentialsProvider(getAwsCredentials()).region(Region.US_EAST_1).build();
    }

    @Bean
    public ConfigsBuilder createConfigBuilder(EventProcessorFactory eventProcessorFactory,
                                              KinesisAsyncClient kinesisAsyncClient,
                                              DynamoDbAsyncClient dynamoDbAsyncClient,
                                              CloudWatchAsyncClient cloudWatchAsyncClient) {
        ConfigsBuilder configsBuilder = new ConfigsBuilder(streamName,
                appName,
                kinesisAsyncClient,
                dynamoDbAsyncClient,
                cloudWatchAsyncClient,
                UUID.randomUUID().toString(),
                eventProcessorFactory);

        configsBuilder.tableName(tableName);
        return configsBuilder;
    }

    @Bean
    public Scheduler creatScheduler(ConfigsBuilder configsBuilder) {
        ProcessorConfig processorConfig = configsBuilder.processorConfig()
                .callProcessRecordsEvenForEmptyRecordList(false);

        MetricsConfig metricsConfig = configsBuilder.metricsConfig()
                .metricsLevel(MetricsLevel.NONE);

        LeaseManagementConfig leaseManagementConfig = configsBuilder.leaseManagementConfig()
                .cleanupLeasesUponShardCompletion(true)
                .maxLeasesForWorker(25)
                .maxLeasesToStealAtOneTime(1)
                .consistentReads(false);
        RetrievalConfig retrievalConfig = configsBuilder.retrievalConfig()
                .initialPositionInStreamExtended(newInitialPosition(TRIM_HORIZON));

        return new Scheduler(configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                leaseManagementConfig,
                configsBuilder.lifecycleConfig(),
                metricsConfig,
                processorConfig,
                retrievalConfig);
    }

    private AwsCredentialsProvider getAwsCredentials() {
        if (awsCredentialsProvider == null) {
            if (isRunningInEC2) {
                awsCredentialsProvider = InstanceProfileCredentialsProvider.builder().build();
            } else if (isRunningInLocal) {
                awsCredentialsProvider = ProfileCredentialsProvider.builder().profileName("doubledigit").build();
            } else {
                awsCredentialsProvider = DefaultCredentialsProvider.builder().build();
            }
        }
        return awsCredentialsProvider;
    }
}
