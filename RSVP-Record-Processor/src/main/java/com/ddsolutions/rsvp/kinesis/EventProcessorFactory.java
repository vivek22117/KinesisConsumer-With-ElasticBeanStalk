package com.ddsolutions.rsvp.kinesis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

import java.util.function.Supplier;

@Component(BeanDefinition.SCOPE_PROTOTYPE)
public class EventProcessorFactory implements ShardRecordProcessorFactory {

    private Supplier<KinesisRecordProcessor> kinesisRecordProcessor;

    @Autowired
    public EventProcessorFactory(Supplier<KinesisRecordProcessor> kinesisRecordProcessor) {
        this.kinesisRecordProcessor = kinesisRecordProcessor;
    }

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return kinesisRecordProcessor.get();
    }
}
