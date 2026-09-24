package org.jsmart.zerocode.core.s3.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jsmart.zerocode.core.s3.download.S3Downloader;
import org.jsmart.zerocode.core.s3.list.S3Lister;
import org.jsmart.zerocode.core.s3.upload.S3Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

/**
 * Central dispatcher for S3 operations in Zerocode test scenarios.
 *
 * <p>Mirrors the pattern of {@code BasicKafkaClient}. Routes S3 operation names
 * (upload, download, list) to the appropriate handler class.
 *
 * <p>Credential resolution order (as specified in issue #742):
 * <ol>
 *   <li>Environment variables: {@code AWS_ACCESS_KEY_ID}, {@code AWS_SECRET_ACCESS_KEY},
 *       {@code AWS_SESSION_TOKEN} (optional)</li>
 *   <li>Properties file: {@code s3.accessKey}, {@code s3.secretKey},
 *       {@code s3.token} (optional)</li>
 *   <li>AWS Default Credential Provider Chain (environment, ~/.aws/credentials, IAM roles, etc.)</li>
 * </ol>
 *
 * <p>Optional properties:
 * <ul>
 *   <li>{@code s3.endpoint} – custom S3-compatible endpoint (e.g. MinIO, LocalStack)</li>
 *   <li>{@code s3.region} – AWS region (defaults to {@code us-east-1})</li>
 * </ul>
 *
 * <p>Step URL format: {@code s3-bucket:<bucket-name>}
 *
 * <p>Supported operations:
 * <ul>
 *   <li>{@code upload} – upload a local file to S3</li>
 *   <li>{@code download} – download an S3 object to a local path</li>
 *   <li>{@code list} – list objects in a bucket (optionally filtered by prefix)</li>
 * </ul>
 */
public class BasicS3Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicS3Client.class);

    @Inject
    private S3Uploader uploader;

    @Inject
    private S3Downloader downloader;

    @Inject
    private S3Lister lister;

    @Inject(optional = true)
    @Named("s3.accessKey")
    private String accessKey;

    @Inject(optional = true)
    @Named("s3.secretKey")
    private String secretKey;

    @Inject(optional = true)
    @Named("s3.region")
    private String region;

    @Inject(optional = true)
    @Named("s3.endpoint")
    private String endpoint;

    @Inject(optional = true)
    @Named("s3.token")
    private String sessionToken;

    public BasicS3Client() {
    }

    /**
     * Executes an S3 operation.
     *
     * @param bucketName  the S3 bucket name (extracted from step URL after "s3-bucket:")
     * @param operation   the operation to perform: upload, download, or list
     * @param requestJson the step request JSON
     * @return JSON result string
     */
    public String execute(String bucketName, String operation, String requestJson) {
        LOGGER.debug("S3 operation: bucket={}, operation={}", bucketName, operation);

        S3Client s3Client = buildS3Client();

        try {
            switch (operation.toLowerCase()) {
                case "upload":
                case "put":
                    return uploader.upload(s3Client, bucketName, requestJson);

                case "download":
                case "get":
                    return downloader.download(s3Client, bucketName, requestJson);

                case "list":
                    return lister.list(s3Client, bucketName, requestJson);

                default:
                    throw new RuntimeException("Unsupported S3 operation: '" + operation +
                            "'. Supported operations: upload, download, list");
            }
        } catch (RuntimeException e) {
            LOGGER.error("S3 operation '{}' failed for bucket '{}': {}", operation, bucketName, e.getMessage());
            throw e;
        } finally {
            s3Client.close();
        }
    }

    /**
     * Builds an S3Client with credential priority:
     * 1) Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN)
     * 2) Properties file (s3.accessKey, s3.secretKey, s3.token)
     * 3) AWS Default Credentials Provider Chain
     */
    S3Client buildS3Client() {
        AwsCredentialsProvider credentialsProvider = resolveCredentials();

        Region awsRegion = (region != null && !region.isEmpty())
                ? Region.of(region)
                : Region.US_EAST_1;

        S3ClientBuilder builder = S3Client.builder()
                .region(awsRegion)
                .credentialsProvider(credentialsProvider);

        if (endpoint != null && !endpoint.isEmpty()) {
            LOGGER.debug("Using custom S3 endpoint: {}", endpoint);
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    private AwsCredentialsProvider resolveCredentials() {
        // 1) Check environment variables first
        String envAccessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String envSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String envSessionToken = System.getenv("AWS_SESSION_TOKEN");

        if (isNotBlank(envAccessKey) && isNotBlank(envSecretKey)) {
            LOGGER.debug("Using AWS credentials from environment variables");
            if (isNotBlank(envSessionToken)) {
                return StaticCredentialsProvider.create(
                        AwsSessionCredentials.create(envAccessKey, envSecretKey, envSessionToken));
            }
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(envAccessKey, envSecretKey));
        }

        // 2) Check properties file
        if (isNotBlank(accessKey) && isNotBlank(secretKey)) {
            LOGGER.debug("Using AWS credentials from properties file");
            if (isNotBlank(sessionToken)) {
                return StaticCredentialsProvider.create(
                        AwsSessionCredentials.create(accessKey, secretKey, sessionToken));
            }
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey));
        }

        // 3) Fall back to AWS default credential provider chain
        LOGGER.debug("No explicit credentials found; using AWS default credential provider chain");
        return DefaultCredentialsProvider.create();
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
