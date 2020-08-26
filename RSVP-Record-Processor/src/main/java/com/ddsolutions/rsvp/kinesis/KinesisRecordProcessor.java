package com.ddsolutions.rsvp.kinesis;

import com.ddsolutions.rsvp.processor.DataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class KinesisRecordProcessor implements ShardRecordProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(KinesisRecordProcessor.class);
    private static final long CHECK_POINT_INTERVAL = 60000L;
    private long nextCheckPointTime;
    private String shardId;

    private DataProcessor dataProcessor;

    @Autowired
    public KinesisRecordProcessor(DataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        this.shardId = initializationInput.shardId();
        LOGGER.info("Initializing record processor for shard: {}", shardId);
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        LOGGER.info("Received " + processRecordsInput.records().size() + " records");

        processRecordsInput.records()
                .forEach(record -> {
                    LOGGER.info("PartitionKey: " +record.partitionKey());
                    LOGGER.info("SequenceNumber: " +record.sequenceNumber());
                    try {
                        dataProcessor.processor(record);
                        LOGGER.debug("record processing done!");
                    } catch (Exception ex) {
                        LOGGER.error("Exception occurred while processing record: {}", record);
                    }
                });
        if (System.currentTimeMillis() > nextCheckPointTime) {
            List<String> recordsSequenceNumbers = processRecordsInput.records()
                    .stream().map(KinesisClientRecord::sequenceNumber)
                    .collect(Collectors.toList());
            try {
                processRecordsInput.checkpointer()
                        .checkpoint(recordsSequenceNumbers.get(recordsSequenceNumbers.size() - 1));
            } catch (InvalidStateException | ShutdownException ex) {
                //Table Does Not Exist
                //Two Processors are processing the same shard
                LOGGER.error("Invalid state while check pointing", ex);
            }
            nextCheckPointTime = System.currentTimeMillis() + CHECK_POINT_INTERVAL;
        }
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        LOGGER.error("Lost lease, so terminating shardId  {}", shardId);
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        try {
            LOGGER.debug("Reached shard end now check pointing, shardId {}", shardId);
            shardEndedInput.checkpointer().checkpoint();
        } catch (InvalidStateException | ShutdownException ex) {
            LOGGER.error("Invalid state while check pointing when shard ended", ex);
        }
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        try {
            LOGGER.debug("Scheduler is shutting down, check pointing, shardId: {}", shardId);
            shutdownRequestedInput.checkpointer().checkpoint();
        } catch (InvalidStateException | ShutdownException ex) {
            LOGGER.error("Invalid state while check pointing when shutdown requested", ex);
        }
    }
}
