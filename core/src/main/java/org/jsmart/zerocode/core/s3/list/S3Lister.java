package org.jsmart.zerocode.core.s3.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.s3.domain.ObjectInfo;
import org.jsmart.zerocode.core.s3.domain.S3Request;
import org.jsmart.zerocode.core.s3.domain.S3Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.jsmart.zerocode.core.constants.ZerocodeConstants.OK;

/**
 * Handles S3 list operations for Zerocode test steps.
 *
 * <p>Supports two listing modes driven by the {@code maxKeys} field in the request:
 *
 * <ul>
 *   <li><b>Auto-paginate mode</b> (default, when {@code maxKeys} is absent): transparently
 *       issues as many {@code ListObjectsV2} calls as needed and returns all matching objects
 *       in a single response with {@code truncated: false}.</li>
 *   <li><b>Manual pagination mode</b> (when {@code maxKeys} is set): returns at most
 *       {@code maxKeys} objects per call. If the result is truncated, {@code truncated: true}
 *       and {@code nextContinuationToken} are included in the response. Pass that token back
 *       as {@code continuationToken} in the next step to fetch the following page.</li>
 * </ul>
 *
 * <p>Each object in the response carries full metadata: {@code key}, {@code size},
 * {@code lastModified} (ISO-8601 UTC), and {@code storageClass}.
 */
public class S3Lister {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3Lister.class);

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Inject
    private ObjectMapper objectMapper;

    public S3Lister() {
    }

    /**
     * Lists objects in an S3 bucket with full metadata and pagination support.
     *
     * @param s3Client   configured AWS S3Client
     * @param bucketName bucket name to list
     * @param requestJson JSON string with optional prefix, maxKeys, and continuationToken
     * @return JSON string with status, list of ObjectInfo, truncation flag, and optional next token
     */
    public String list(S3Client s3Client, String bucketName, String requestJson) {
        try {
            S3Request request = objectMapper.readValue(requestJson, S3Request.class);

            S3Response response = (request.getMaxKeys() != null)
                    ? listSinglePage(s3Client, bucketName, request)
                    : listAllPages(s3Client, bucketName, request);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            LOGGER.error("S3 list failed for bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("S3 list failed: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches a single page of at most {@code maxKeys} objects. If more objects exist
     * beyond this page, the response will have {@code truncated: true} and a
     * {@code nextContinuationToken} the caller can use in the next step.
     */
    private S3Response listSinglePage(S3Client s3Client, String bucketName, S3Request request) {
        ListObjectsV2Request.Builder builder = baseBuilder(bucketName, request)
                .maxKeys(request.getMaxKeys());

        if (request.getContinuationToken() != null && !request.getContinuationToken().isEmpty()) {
            builder.continuationToken(request.getContinuationToken());
        }

        ListObjectsV2Response page = s3Client.listObjectsV2(builder.build());
        List<ObjectInfo> objects = toObjectInfoList(page.contents());

        LOGGER.info("Listed {} objects in s3://{} (truncated={})", objects.size(), bucketName, page.isTruncated());

        S3Response response = new S3Response(OK);
        response.setObjects(objects);
        response.setTruncated(page.isTruncated());
        if (page.isTruncated()) {
            response.setNextContinuationToken(page.nextContinuationToken());
        }
        return response;
    }

    /**
     * Transparently pages through all results and returns every object in a single
     * response. {@code truncated} is always {@code false} in this mode.
     */
    private S3Response listAllPages(S3Client s3Client, String bucketName, S3Request request) {
        List<ObjectInfo> allObjects = new ArrayList<>();
        String continuationToken = null;

        do {
            ListObjectsV2Request.Builder builder = baseBuilder(bucketName, request);
            if (continuationToken != null) {
                builder.continuationToken(continuationToken);
            }

            ListObjectsV2Response page = s3Client.listObjectsV2(builder.build());
            allObjects.addAll(toObjectInfoList(page.contents()));
            continuationToken = page.isTruncated() ? page.nextContinuationToken() : null;

            LOGGER.debug("Fetched page: {} objects, truncated={}", page.contents().size(), page.isTruncated());

        } while (continuationToken != null);

        LOGGER.info("Listed {} total objects in s3://{}", allObjects.size(), bucketName);

        S3Response response = new S3Response(OK);
        response.setObjects(allObjects);
        response.setTruncated(false);
        return response;
    }

    private ListObjectsV2Request.Builder baseBuilder(String bucketName, S3Request request) {
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder().bucket(bucketName);
        if (request.getPrefix() != null && !request.getPrefix().isEmpty()) {
            builder.prefix(request.getPrefix());
        }
        return builder;
    }

    private List<ObjectInfo> toObjectInfoList(List<S3Object> s3Objects) {
        List<ObjectInfo> result = new ArrayList<>();
        for (S3Object s3Object : s3Objects) {
            String lastModified = s3Object.lastModified() != null
                    ? ISO_FORMATTER.format(s3Object.lastModified())
                    : null;
            String storageClass = s3Object.storageClassAsString();
            result.add(new ObjectInfo(s3Object.key(), s3Object.size(), lastModified, storageClass));
        }
        return result;
    }
}
