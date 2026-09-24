package org.jsmart.zerocode.core.s3.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.s3.domain.S3Request;
import org.jsmart.zerocode.core.s3.domain.S3Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;

import static org.jsmart.zerocode.core.constants.ZerocodeConstants.OK;

/**
 * Handles S3 upload operations for Zerocode test steps.
 *
 * <p>Uploads a local file to the specified S3 bucket and key.
 * The {@code file} field in the step request is resolved from the classpath root
 * or as an absolute/relative file path.
 */
public class S3Uploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3Uploader.class);

    @Inject
    private ObjectMapper objectMapper;

    public S3Uploader() {
    }

    /**
     * Uploads a file to S3.
     *
     * @param s3Client   configured AWS S3Client
     * @param bucketName destination bucket name
     * @param requestJson JSON string with "key" (S3 object key) and "file" (local file path)
     * @return JSON string with status and s3Url
     */
    public String upload(S3Client s3Client, String bucketName, String requestJson) {
        try {
            S3Request request = objectMapper.readValue(requestJson, S3Request.class);

            if (request.getKey() == null || request.getKey().isEmpty()) {
                throw new IllegalArgumentException("S3 upload requires 'key' in the request");
            }
            if (request.getFile() == null || request.getFile().isEmpty()) {
                throw new IllegalArgumentException("S3 upload requires 'file' in the request");
            }

            String localFilePath = resolveFilePath(request.getFile());

            LOGGER.debug("Uploading file '{}' to s3://{}/{}", localFilePath, bucketName, request.getKey());

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(request.getKey())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(Paths.get(localFilePath)));

            S3Response response = new S3Response(OK);
            response.setS3Url("s3://" + bucketName + "/" + request.getKey());

            LOGGER.info("Successfully uploaded to s3://{}/{}", bucketName, request.getKey());

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            LOGGER.error("S3 upload failed for bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }

    private String resolveFilePath(String file) {
        // Try classpath first, then treat as file system path
        java.net.URL resource = getClass().getClassLoader().getResource(file);
        if (resource != null) {
            try {
                return Paths.get(resource.toURI()).toString();
            } catch (java.net.URISyntaxException e) {
                LOGGER.warn("Could not convert resource URL to URI: {}", resource, e);
            }
        }
        return file;
    }
}
