package org.jsmart.zerocode.core.s3;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringUtils;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicS3Client implements S3Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicS3Client.class);
    private static final String FAILED = "Failed";

    @Inject
    private ObjectMapper objectMapper;

    @Inject(optional = true)
    @Named("s3.accessKey")
    private String s3AccessKey;

    @Inject(optional = true)
    @Named("s3.secretKey")
    private String s3SecretKey;

    @Inject(optional = true)
    @Named("s3.region")
    private String s3Region;

    @Inject(optional = true)
    @Named("s3.token")
    private String s3Token;

    @Inject(optional = true)
    @Named("s3.endpoint")
    private String s3Endpoint;

    public BasicS3Client() {
    }

    private AmazonS3 buildS3Client() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();

        String envAccessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String envSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String envSessionToken = System.getenv("AWS_SESSION_TOKEN");
        String envRegion = System.getenv("AWS_REGION");

        String finalAccessKey = StringUtils.isNotBlank(envAccessKey) ? envAccessKey : s3AccessKey;
        String finalSecretKey = StringUtils.isNotBlank(envSecretKey) ? envSecretKey : s3SecretKey;
        String finalToken = StringUtils.isNotBlank(envSessionToken) ? envSessionToken : s3Token;
        String finalRegion = StringUtils.isNotBlank(envRegion) ? envRegion : s3Region;

        if (StringUtils.isNotBlank(finalAccessKey) && StringUtils.isNotBlank(finalSecretKey)) {
            AWSCredentialsProvider credentialsProvider;
            if (StringUtils.isNotBlank(finalToken)) {
                credentialsProvider = new AWSStaticCredentialsProvider(new BasicSessionCredentials(finalAccessKey, finalSecretKey, finalToken));
            } else {
                credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(finalAccessKey, finalSecretKey));
            }
            builder.withCredentials(credentialsProvider);
        } else {
            // fallback to default provider chain
            builder.withCredentials(new DefaultAWSCredentialsProviderChain());
        }

        if (StringUtils.isNotBlank(s3Endpoint)) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Endpoint, finalRegion));
        } else if (StringUtils.isNotBlank(finalRegion)) {
            builder.withRegion(finalRegion);
        }

        return builder.build();
    }

    @Override
    public String execute(String bucketName, String operation, String requestJson, ScenarioExecutionState scenarioExecutionState) {
        LOGGER.debug("S3 operation: {}, bucket: {}, request: {}", operation, bucketName, requestJson);

        try {
            AmazonS3 s3Client = buildS3Client();

            String actionName = operation;
            if (actionName.contains(".")) {
                actionName = actionName.substring(actionName.indexOf('.') + 1);
            }
            S3Action s3Action = S3Action.fromString(actionName);

            JsonNode requestNode = null;
            if (StringUtils.isNotBlank(requestJson)) {
                requestNode = objectMapper.readTree(requestJson);
            }

            switch (s3Action) {
                case UPLOAD:
                    return handleUpload(s3Client, bucketName, requestNode);
                case DOWNLOAD:
                    return handleDownload(s3Client, bucketName, requestNode);
                case LIST:
                    return handleList(s3Client, bucketName, requestNode);
                default:
                    throw new RuntimeException("Unsupported S3 operation: " + operation);
            }
        } catch (Throwable e) {
            LOGGER.error("Error executing S3 operation: {}", e.getMessage(), e);
            try {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", FAILED);
                errorResponse.put("message", e.getMessage());
                return objectMapper.writeValueAsString(errorResponse);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(e);
            }
        }
    }

    private String handleUpload(AmazonS3 s3Client, String bucket, JsonNode requestNode) throws JsonProcessingException {
        validateRequestNode(requestNode);
        String key = getRequiredString(requestNode, "key");
        String filePath = getRequiredString(requestNode, "filePath");

        File file = validateAndGetFile(filePath);

        s3Client.putObject(bucket, key, file);

        Map<String, Object> result = new HashMap<>();
        result.put("status", 200);
        result.put("bucket", bucket);
        result.put("key", key);

        return objectMapper.writeValueAsString(result);
    }

    private String handleDownload(AmazonS3 s3Client, String bucket, JsonNode requestNode) throws JsonProcessingException {
        validateRequestNode(requestNode);
        String key = getRequiredString(requestNode, "key");
        String saveAs = getRequiredString(requestNode, "saveAs");

        File outFile = new File(saveAs);
        if (!outFile.isAbsolute() && !saveAs.startsWith("/")) {
            outFile = new File("target/" + saveAs);
        }
        
        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        s3Client.getObject(new com.amazonaws.services.s3.model.GetObjectRequest(bucket, key), outFile);

        Map<String, Object> result = new HashMap<>();
        result.put("downloaded", true);
        result.put("savedTo", outFile.getAbsolutePath());

        return objectMapper.writeValueAsString(result);
    }

    private String handleList(AmazonS3 s3Client, String bucket, JsonNode requestNode) throws JsonProcessingException {
        String folder = null;
        if (requestNode != null && requestNode.has("folder") && !requestNode.get("folder").isNull()) {
            folder = requestNode.get("folder").asText();
        }

        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket);
        if (StringUtils.isNotBlank(folder)) {
            req.withPrefix(folder);
        }

        ListObjectsV2Result objectListing = s3Client.listObjectsV2(req);
        List<Map<String, Object>> files = new ArrayList<>();
        
        for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("key", summary.getKey());
            fileInfo.put("size", summary.getSize());
            fileInfo.put("lastModified", summary.getLastModified().getTime());
            files.add(fileInfo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("files", files);
        result.put("files.SIZE", files.size());

        return objectMapper.writeValueAsString(result);
    }

    private void validateRequestNode(JsonNode requestNode) {
        if (requestNode == null) {
            throw new IllegalArgumentException("Missing required request body for S3 operation.");
        }
    }

    private String getRequiredString(JsonNode node, String fieldName) {
        if (!node.has(fieldName) || node.get(fieldName).isNull()) {
            throw new IllegalArgumentException("Missing required field: " + fieldName);
        }
        return node.get(fieldName).asText();
    }

    private File validateAndGetFile(String fileName) {
        File absoluteFile = new File(fileName);
        if (absoluteFile.exists()) {
            return absoluteFile;
        }

        try {
            URL resource = getClass().getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new IllegalArgumentException("File does not exist: " + fileName);
            }
            return new File(resource.getFile());
        } catch (Exception ex) {
            throw new RuntimeException("Error accessing file: `" + fileName + "' - " + ex);
        }
    }
}
