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
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.leases.LeaseManagementConfig;
import software.amazon.kinesis.metrics.MetricsConfig;
import software.amazon.kinesis.metrics.MetricsLevel;
import software.amazon.kinesis.processor.ProcessorConfig;

import java.util.UUID;
import java.util.function.Supplier;

import static com.ddsolutions.rsvp.utility.PropertyLoaderUtility.getInstance;
import static java.lang.Boolean.parseBoolean;
import static org.slf4j.LoggerFactory.getLogger;
import static software.amazon.kinesis.common.InitialPositionInStream.TRIM_HORIZON;

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
                .callProcessRecordsEvenForEmptyRecordList(true);

        MetricsConfig metricsConfig = configsBuilder.metricsConfig().metricsLevel(MetricsLevel.NONE);

        LeaseManagementConfig leaseManagementConfig =
                configsBuilder.leaseManagementConfig()
                        .cleanupLeasesUponShardCompletion(true)
                        .maxLeasesForWorker(25)
                        .initialLeaseTableReadCapacity(5)
                        .initialLeaseTableWriteCapacity(5)
                        .maxLeasesToStealAtOneTime(1)
                        .consistentReads(false);

        return new Scheduler(configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                leaseManagementConfig,
                configsBuilder.lifecycleConfig(),
                metricsConfig,
                processorConfig,
                configsBuilder.retrievalConfig());
    }

    private static AwsCredentialsProvider getAwsCredentials() {
        if (awsCredentialsProvider == null) {
            boolean isRunningInEC2 = parseBoolean(getInstance().getProperty("isRunningInEC2"));
            boolean isRunningInLocal = parseBoolean(getInstance().getProperty("isRunningInLocal"));
            if (isRunningInEC2) {
                awsCredentialsProvider = InstanceProfileCredentialsProvider.builder().build();
                return awsCredentialsProvider;
            } else if (isRunningInLocal) {
                awsCredentialsProvider = ProfileCredentialsProvider.builder().profileName("doubledigit").build();
                return awsCredentialsProvider;
            } else {
                awsCredentialsProvider = DefaultCredentialsProvider.builder().build();
            }
        }
        return awsCredentialsProvider;
    }
}
