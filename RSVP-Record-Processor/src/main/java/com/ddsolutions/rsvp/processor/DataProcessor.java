package com.ddsolutions.rsvp.processor;

import com.ddsolutions.rsvp.domain.RSVPEventRecord;
import com.ddsolutions.rsvp.utility.GzipUtility;
import com.ddsolutions.rsvp.utility.JsonUtility;
import com.ddsolutions.rsvp.utility.S3Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DataProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);
    private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
    private static List<RSVPEventRecord> listOfRsvpRecords = new ArrayList<>();
    private static final String DELIMITER = "/";

    private JsonUtility jsonUtility;
    private S3Utils s3Utils;

    @Autowired
    public DataProcessor(final JsonUtility jsonUtility, final S3Utils s3Utils) {
        this.jsonUtility = jsonUtility;
        this.s3Utils = s3Utils;
    }

    public void processor(KinesisClientRecord record) {
        String data = null;
        try {
            data = decoder.decode(record.data()).toString();
            String decompressedData = GzipUtility.decompressData(data.getBytes());
            String deserializeData = GzipUtility.deserializeData(decompressedData);
            RSVPEventRecord rsvpEventRecord = jsonUtility.convertFromJson(deserializeData, RSVPEventRecord.class);

            collectAndPersist(rsvpEventRecord);

            LOGGER.debug("Processing done!");
        } catch (CharacterCodingException ex) {
            LOGGER.error("Malformed data: {}", data, ex);
        } catch (IOException ex) {
            LOGGER.error("Json parsing failed: {}", data, ex);
        }

    }

    private void collectAndPersist(RSVPEventRecord rsvpEventRecord) throws IOException {
        if (rsvpEventRecord != null) {
            int batchSize = 10;
            listOfRsvpRecords.add(rsvpEventRecord);
            if (listOfRsvpRecords.size() == batchSize) {
                String s3Key = createS3Key(listOfRsvpRecords);
                s3Utils.putFileToS3(listOfRsvpRecords, true, s3Key);
                listOfRsvpRecords = new ArrayList<>();
            }
        }
    }

    private String createS3Key(List<RSVPEventRecord> listOfRsvpRecords) {
        LocalDateTime date = LocalDateTime.now();
        listOfRsvpRecords.sort(Comparator.comparingLong(RSVPEventRecord::getMtime).reversed());
        return new StringBuilder().append("data").append(DELIMITER)
                .append("rsvp").append(DELIMITER)
                .append(date.getYear()).append(DELIMITER)
                .append(date.getMonthValue()).append(DELIMITER)
                .append(date.getDayOfMonth()).append(DELIMITER)
                .append(Instant.now().toEpochMilli()).append("_rsvp.records").toString();
    }
}
