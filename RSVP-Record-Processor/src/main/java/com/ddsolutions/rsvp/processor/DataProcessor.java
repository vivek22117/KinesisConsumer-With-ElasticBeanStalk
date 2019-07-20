package com.ddsolutions.rsvp.processor;

import com.ddsolutions.rsvp.domain.RSVPEventRecord;
import com.ddsolutions.rsvp.utility.GzipUtility;
import com.ddsolutions.rsvp.utility.JsonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

@Component
public class DataProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);
    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    private JsonUtility jsonUtility;

    @Autowired
    public DataProcessor(JsonUtility jsonUtility) {
        this.jsonUtility = jsonUtility;
    }

    public void processor(KinesisClientRecord record) {
        String data = null;
        try {
            data = decoder.decode(record.data()).toString();
            String decompressedData = GzipUtility.decompressData(data.getBytes());
            String deserializeData = GzipUtility.deserializeData(decompressedData);
            RSVPEventRecord rsvpEventRecord = jsonUtility.convertFromJson(deserializeData, RSVPEventRecord.class);

            LOGGER.debug("Processing done!");
        } catch (CharacterCodingException ex) {
            LOGGER.error("Malformed data: {}", data, ex);
        } catch (IOException ex) {
            LOGGER.error("json parsing failed: {}", data, ex);
        }

    }
}
