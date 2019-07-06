package com.ddsolutions.rsvp.kinesis;

import com.ddsolutions.rsvp.processor.DataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class KinesisRecordProcessor implements ShardRecordProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(KinesisRecordProcessor.class);
    private static final long CHECK_POINT_INTERVAL = 60000L;
    private long nextCheckPointTime;

    private DataProcessor dataProcessor;

    @Autowired
    public KinesisRecordProcessor(DataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        LOGGER.info("Initializing record processor for shard: {}", initializationInput.shardId());
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        LOGGER.info("Received " + processRecordsInput.records().size() + " records");
        processRecordsInput.records().parallelStream().forEach(record -> dataProcessor.processor(record));
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {

    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {

    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {

    }
}
