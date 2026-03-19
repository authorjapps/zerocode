package org.jsmart.zerocode.core.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.s3.domain.ObjectInfo;
import org.jsmart.zerocode.core.s3.domain.S3Response;
import org.jsmart.zerocode.core.s3.download.S3Downloader;
import org.jsmart.zerocode.core.s3.list.S3Lister;
import org.jsmart.zerocode.core.s3.upload.S3Uploader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.constants.ZerocodeConstants.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for S3 operation handlers using mocked AWS S3Client.
 *
 * <p>Each test verifies that the correct AWS SDK calls are made and
 * that proper Zerocode responses are generated. No external S3 service
 * or embedded mock server is needed.
 */
public class S3IntegrationTest {

    private static final String BUCKET = "zerocode-test-bucket";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    private S3Client s3Client;

    private ObjectMapper objectMapper;

    private S3Uploader uploader;
    private S3Downloader downloader;
    private S3Lister lister;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        objectMapper = new ObjectMapper();

        uploader = new S3Uploader();
        injectField(uploader, "objectMapper", objectMapper);

        downloader = new S3Downloader();
        injectField(downloader, "objectMapper", objectMapper);

        lister = new S3Lister();
        injectField(lister, "objectMapper", objectMapper);
    }

    // -------------------------------------------------------------------
    // Upload tests
    // -------------------------------------------------------------------

    @Test
    public void testUpload_returnsOkStatusAndS3Url() throws Exception {
        File localFile = tempFolder.newFile("upload-test.txt");
        writeContent(localFile, "hello zerocode");

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String requestJson = objectMapper.createObjectNode()
                .put("key", "reports/upload-test.txt")
                .put("file", localFile.getAbsolutePath())
                .toString();

        String resultJson = uploader.upload(s3Client, BUCKET, requestJson);

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));
        assertThat(response.getS3Url(), is("s3://" + BUCKET + "/reports/upload-test.txt"));

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        assertThat(captor.getValue().bucket(), is(BUCKET));
        assertThat(captor.getValue().key(), is("reports/upload-test.txt"));
    }

    @Test(expected = RuntimeException.class)
    public void testUpload_missingKey_throwsException() throws Exception {
        File localFile = tempFolder.newFile("no-key.txt");

        String requestJson = objectMapper.createObjectNode()
                .put("file", localFile.getAbsolutePath())
                .toString();

        uploader.upload(s3Client, BUCKET, requestJson);
    }

    @Test(expected = RuntimeException.class)
    public void testUpload_missingFile_throwsException() {
        String requestJson = objectMapper.createObjectNode()
                .put("key", "some/key.txt")
                .toString();

        uploader.upload(s3Client, BUCKET, requestJson);
    }

    // -------------------------------------------------------------------
    // Download tests
    // -------------------------------------------------------------------

    @Test
    public void testDownload_invokesGetObjectWithCorrectParams() throws Exception {
        File dest = new File(tempFolder.getRoot(), "dest.txt");

        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> responseStream =
                new ResponseInputStream<>(getObjectResponse, new ByteArrayInputStream("content".getBytes()));

        when(s3Client.getObject(any(GetObjectRequest.class), any(java.nio.file.Path.class)))
                .thenReturn(getObjectResponse);

        String requestJson = objectMapper.createObjectNode()
                .put("key", "dl/source.txt")
                .put("localPath", dest.getAbsolutePath())
                .toString();

        String resultJson = downloader.download(s3Client, BUCKET, requestJson);

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture(), any(java.nio.file.Path.class));
        assertThat(captor.getValue().bucket(), is(BUCKET));
        assertThat(captor.getValue().key(), is("dl/source.txt"));
    }

    @Test(expected = RuntimeException.class)
    public void testDownload_missingKey_throwsException() throws Exception {
        File dest = tempFolder.newFile("dest.txt");

        String requestJson = objectMapper.createObjectNode()
                .put("localPath", dest.getAbsolutePath())
                .toString();

        downloader.download(s3Client, BUCKET, requestJson);
    }

    @Test(expected = RuntimeException.class)
    public void testDownload_missingLocalPath_throwsException() {
        String requestJson = objectMapper.createObjectNode()
                .put("key", "some/key.txt")
                .toString();

        downloader.download(s3Client, BUCKET, requestJson);
    }

    // -------------------------------------------------------------------
    // List tests — metadata
    // -------------------------------------------------------------------

    @Test
    public void testList_returnsObjectInfoWithMetadata() throws Exception {
        S3Object obj1 = S3Object.builder()
                .key("meta/file-a.txt").size(100L)
                .lastModified(Instant.parse("2024-03-15T10:30:00Z"))
                .storageClass("STANDARD").build();
        S3Object obj2 = S3Object.builder()
                .key("meta/file-b.txt").size(200L)
                .lastModified(Instant.parse("2024-03-16T08:00:00Z"))
                .storageClass("STANDARD").build();

        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(Arrays.asList(obj1, obj2))
                .isTruncated(false)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        String requestJson = objectMapper.createObjectNode()
                .put("prefix", "meta/")
                .toString();

        String resultJson = lister.list(s3Client, BUCKET, requestJson);

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));
        assertThat(response.getTruncated(), is(false));

        List<ObjectInfo> objects = response.getObjects();
        assertEquals(2, objects.size());

        ObjectInfo first = objects.get(0);
        assertThat(first.getKey(), is("meta/file-a.txt"));
        assertThat(first.getSize(), is(100L));
        assertThat(first.getLastModified(), notNullValue());
        assertThat(first.getStorageClass(), is("STANDARD"));
    }

    @Test
    public void testList_emptyPrefix_returnsAllObjects() throws Exception {
        S3Object obj1 = S3Object.builder()
                .key("x.txt").size(1L)
                .lastModified(Instant.now())
                .storageClass("STANDARD").build();

        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(Collections.singletonList(obj1))
                .isTruncated(false)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        String resultJson = lister.list(s3Client, BUCKET, "{}");

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));
        assertThat(response.getTruncated(), is(false));
        assertTrue("Should have at least 1 object", response.getObjects().size() >= 1);
    }

    @Test
    public void testList_metadataAccuracy() throws Exception {
        S3Object obj = S3Object.builder()
                .key("size-check/known-size.txt").size(16L)
                .lastModified(Instant.parse("2024-01-01T00:00:00Z"))
                .storageClass("STANDARD").build();

        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(Collections.singletonList(obj))
                .isTruncated(false)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        String requestJson = objectMapper.createObjectNode()
                .put("prefix", "size-check/")
                .toString();

        String resultJson = lister.list(s3Client, BUCKET, requestJson);

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));
        assertEquals(1, response.getObjects().size());

        ObjectInfo info = response.getObjects().get(0);
        assertThat(info.getKey(), is("size-check/known-size.txt"));
        assertThat(info.getSize(), is(16L));
        assertThat(info.getLastModified(), notNullValue());
        assertThat(info.getStorageClass(), notNullValue());
    }

    // -------------------------------------------------------------------
    // List tests — pagination
    // -------------------------------------------------------------------

    @Test
    public void testList_manualPagination_firstPage_isTruncated() throws Exception {
        S3Object obj1 = S3Object.builder().key("page/a.txt").size(1L)
                .lastModified(Instant.now()).storageClass("STANDARD").build();
        S3Object obj2 = S3Object.builder().key("page/b.txt").size(1L)
                .lastModified(Instant.now()).storageClass("STANDARD").build();

        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(Arrays.asList(obj1, obj2))
                .isTruncated(true)
                .nextContinuationToken("token-123")
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        String requestJson = objectMapper.createObjectNode()
                .put("prefix", "page/")
                .put("maxKeys", 2)
                .toString();

        String resultJson = lister.list(s3Client, BUCKET, requestJson);

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));
        assertEquals(2, response.getObjects().size());
        assertThat(response.getTruncated(), is(true));
        assertThat(response.getNextContinuationToken(), is("token-123"));
    }

    @Test
    public void testList_manualPagination_secondPage_notTruncated() throws Exception {
        S3Object obj = S3Object.builder().key("p2/c.txt").size(1L)
                .lastModified(Instant.now()).storageClass("STANDARD").build();

        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(Collections.singletonList(obj))
                .isTruncated(false)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        String requestJson = objectMapper.createObjectNode()
                .put("prefix", "p2/")
                .put("maxKeys", 2)
                .put("continuationToken", "some-token")
                .toString();

        String resultJson = lister.list(s3Client, BUCKET, requestJson);

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));
        assertEquals(1, response.getObjects().size());
        assertThat(response.getTruncated(), is(false));
        assertThat(response.getNextContinuationToken(), is(nullValue()));
    }

    @Test
    public void testList_autoPaginate_collectsAllObjects() throws Exception {
        // First page - truncated
        S3Object obj1 = S3Object.builder().key("auto/file-0.txt").size(1L)
                .lastModified(Instant.now()).storageClass("STANDARD").build();
        S3Object obj2 = S3Object.builder().key("auto/file-1.txt").size(1L)
                .lastModified(Instant.now()).storageClass("STANDARD").build();

        ListObjectsV2Response page1 = ListObjectsV2Response.builder()
                .contents(Arrays.asList(obj1, obj2))
                .isTruncated(true)
                .nextContinuationToken("next-token")
                .build();

        // Second page - not truncated
        S3Object obj3 = S3Object.builder().key("auto/file-2.txt").size(1L)
                .lastModified(Instant.now()).storageClass("STANDARD").build();

        ListObjectsV2Response page2 = ListObjectsV2Response.builder()
                .contents(Collections.singletonList(obj3))
                .isTruncated(false)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(page1)
                .thenReturn(page2);

        String requestJson = objectMapper.createObjectNode()
                .put("prefix", "auto/")
                .toString();

        String resultJson = lister.list(s3Client, BUCKET, requestJson);

        S3Response response = objectMapper.readValue(resultJson, S3Response.class);
        assertThat(response.getStatus(), is(OK));
        assertThat(response.getTruncated(), is(false));
        assertEquals(3, response.getObjects().size());
        assertThat(response.getNextContinuationToken(), is(nullValue()));
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private static void writeContent(File file, String content) throws Exception {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
    }

    private static void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
