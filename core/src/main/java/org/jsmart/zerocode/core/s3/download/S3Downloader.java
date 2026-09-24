package org.jsmart.zerocode.core.s3.download;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.s3.domain.S3Request;
import org.jsmart.zerocode.core.s3.domain.S3Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.nio.file.Paths;

import static org.jsmart.zerocode.core.constants.ZerocodeConstants.OK;

/**
 * Handles S3 download operations for Zerocode test steps.
 *
 * <p>Downloads an S3 object to a local file path specified in the step request.
 */
public class S3Downloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3Downloader.class);

    @Inject
    private ObjectMapper objectMapper;

    public S3Downloader() {
    }

    /**
     * Downloads an S3 object to a local file.
     *
     * @param s3Client   configured AWS S3Client
     * @param bucketName source bucket name
     * @param requestJson JSON string with "key" (S3 object key) and "localPath" (destination file path)
     * @return JSON string with status
     */
    public String download(S3Client s3Client, String bucketName, String requestJson) {
        try {
            S3Request request = objectMapper.readValue(requestJson, S3Request.class);

            if (request.getKey() == null || request.getKey().isEmpty()) {
                throw new IllegalArgumentException("S3 download requires 'key' in the request");
            }
            if (request.getLocalPath() == null || request.getLocalPath().isEmpty()) {
                throw new IllegalArgumentException("S3 download requires 'localPath' in the request");
            }

            LOGGER.debug("Downloading s3://{}/{} to '{}'", bucketName, request.getKey(), request.getLocalPath());

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(request.getKey())
                    .build();

            s3Client.getObject(getRequest, Paths.get(request.getLocalPath()));

            S3Response response = new S3Response(OK);

            LOGGER.info("Successfully downloaded s3://{}/{} to '{}'", bucketName, request.getKey(), request.getLocalPath());

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            LOGGER.error("S3 download failed for bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("S3 download failed: " + e.getMessage(), e);
        }
    }
}
