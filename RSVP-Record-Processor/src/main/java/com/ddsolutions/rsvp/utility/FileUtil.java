package com.ddsolutions.rsvp.utility;


import com.ddsolutions.rsvp.domain.RSVPEventRecord;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileUtil {

    private JsonUtility jsonUtility = new JsonUtility();

    public byte[] write(List<RSVPEventRecord> rsvpEventRecords) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (RSVPEventRecord rsvpEventRecord : rsvpEventRecords) {
                stream.write(jsonUtility.convertToString(rsvpEventRecord).getBytes());
                stream.write("\n".getBytes());
            }
            return GzipUtility.compressData(stream.toByteArray());
        }
    }

    public Stream<RSVPEventRecord> read(byte[] compressedData) throws IOException {
        byte[] decompressedData = GzipUtility.decompress(compressedData);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(decompressedData)));
        return bufferedReader.lines().map(rsvpRecord -> {
            try {
                return jsonUtility.convertFromJson(rsvpRecord, RSVPEventRecord.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}