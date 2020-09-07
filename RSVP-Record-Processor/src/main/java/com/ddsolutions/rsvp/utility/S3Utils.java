package com.ddsolutions.rsvp.utility;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.ddsolutions.rsvp.domain.RSVPEventRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;

@Component
public class S3Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3Utils.class);

    private AmazonS3 amazonS3;
    private FileUtil fileUtil;

    @Value("${s3.bucket.name}")
    private String bucketName;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Autowired
    public S3Utils(AmazonS3 amazonS3, FileUtil fileUtil) {
        this.amazonS3 = amazonS3;
        this.fileUtil = fileUtil;
    }

    public List<RSVPEventRecord> getRSVPRecords(List<Map<String, Object>> data) {
        List<RSVPEventRecord> rsvpEventRecords = new ArrayList<>();
        try {
            List<Future<List<RSVPEventRecord>>> futures = new ArrayList<>();
            for (Map<String, Object> map : data) {
                Future<List<RSVPEventRecord>> rsvpRecordFuture =
                        executorService.submit(() -> getRSVPRecords(map.get("KEY").toString()));
                futures.add(rsvpRecordFuture);
            }
            for (Future<List<RSVPEventRecord>> rsvpRecords : futures) {
                rsvpEventRecords.addAll(rsvpRecords.get());
            }
        } catch (Exception e) {
            LOGGER.error("exception while fetching records, message {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        return rsvpEventRecords;
    }

    public void putFileToS3(List<RSVPEventRecord> rsvpEventRecords, boolean isEncrypt, String key) {
        try {
            byte[] fileBytes = fileUtil.write(rsvpEventRecords);
            PutObjectRequest request = createRequest(fileBytes, isEncrypt, key);

            if (isEncrypt) {
                request.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
            }
            amazonS3.putObject(request);
        } catch (Exception ex) {
            LOGGER.error("S3 put failed, exception message {} ", ex.getMessage(), ex);
        }
    }

    private PutObjectRequest createRequest(byte[] fileBytes, boolean isEncrypt, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileBytes.length);
        if (isEncrypt) {
            metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
        }
        return new PutObjectRequest(bucketName, key, new ByteArrayInputStream(fileBytes), metadata);
    }

    private List<RSVPEventRecord> getRSVPRecords(String key) throws Exception {
        try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, key))) {
            return fileUtil.read(IOUtils.toByteArray(s3Object.getObjectContent())).collect(toList());
        } catch (AmazonS3Exception ex) {
            if (ex.getErrorCode().equals("NoSuckKey")) {
                LOGGER.error("No such key present, with message {} ", ex.getMessage(), ex);
                return new ArrayList<>();
            } else {
                throw ex;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while fetching records form bucket {} ", bucketName, e);
            throw e;
        }
    }
}
