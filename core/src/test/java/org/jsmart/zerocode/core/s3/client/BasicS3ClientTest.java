package org.jsmart.zerocode.core.s3.client;

import org.jsmart.zerocode.core.s3.download.S3Downloader;
import org.jsmart.zerocode.core.s3.list.S3Lister;
import org.jsmart.zerocode.core.s3.upload.S3Uploader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3Client;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasicS3ClientTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private S3Uploader uploader;

    @Mock
    private S3Downloader downloader;

    @Mock
    private S3Lister lister;

    @Mock
    private S3Client mockS3Client;

    private BasicS3Client basicS3Client;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        basicS3Client = new BasicS3Client() {
            @Override
            S3Client buildS3Client() {
                return mockS3Client;
            }
        };

        injectField(basicS3Client, "uploader", uploader);
        injectField(basicS3Client, "downloader", downloader);
        injectField(basicS3Client, "lister", lister);
    }

    @Test
    public void testExecute_upload_delegatesToUploader() {
        String bucket = "test-bucket";
        String requestJson = "{\"key\":\"reports/file.json\",\"file\":\"data/file.json\"}";
        when(uploader.upload(any(S3Client.class), eq(bucket), eq(requestJson)))
                .thenReturn("{\"status\":\"Ok\",\"s3Url\":\"s3://test-bucket/reports/file.json\"}");

        String result = basicS3Client.execute(bucket, "upload", requestJson);

        verify(uploader).upload(any(S3Client.class), eq(bucket), eq(requestJson));
        assertThat(result, is("{\"status\":\"Ok\",\"s3Url\":\"s3://test-bucket/reports/file.json\"}"));
    }

    @Test
    public void testExecute_put_delegatesToUploader() {
        String bucket = "test-bucket";
        String requestJson = "{\"key\":\"reports/file.json\",\"file\":\"data/file.json\"}";
        when(uploader.upload(any(S3Client.class), eq(bucket), eq(requestJson)))
                .thenReturn("{\"status\":\"Ok\",\"s3Url\":\"s3://test-bucket/reports/file.json\"}");

        basicS3Client.execute(bucket, "put", requestJson);

        verify(uploader).upload(any(S3Client.class), eq(bucket), eq(requestJson));
    }

    @Test
    public void testExecute_download_delegatesToDownloader() {
        String bucket = "test-bucket";
        String requestJson = "{\"key\":\"reports/file.json\",\"localPath\":\"/tmp/file.json\"}";
        when(downloader.download(any(S3Client.class), eq(bucket), eq(requestJson)))
                .thenReturn("{\"status\":\"Ok\"}");

        String result = basicS3Client.execute(bucket, "download", requestJson);

        verify(downloader).download(any(S3Client.class), eq(bucket), eq(requestJson));
        assertThat(result, is("{\"status\":\"Ok\"}"));
    }

    @Test
    public void testExecute_get_delegatesToDownloader() {
        String bucket = "test-bucket";
        String requestJson = "{\"key\":\"reports/file.json\",\"localPath\":\"/tmp/file.json\"}";
        when(downloader.download(any(S3Client.class), eq(bucket), eq(requestJson)))
                .thenReturn("{\"status\":\"Ok\"}");

        basicS3Client.execute(bucket, "get", requestJson);

        verify(downloader).download(any(S3Client.class), eq(bucket), eq(requestJson));
    }

    @Test
    public void testExecute_list_delegatesToLister() {
        String bucket = "test-bucket";
        String requestJson = "{\"prefix\":\"reports/\"}";
        when(lister.list(any(S3Client.class), eq(bucket), eq(requestJson)))
                .thenReturn("{\"status\":\"Ok\",\"objects\":[\"reports/file.json\"]}");

        String result = basicS3Client.execute(bucket, "list", requestJson);

        verify(lister).list(any(S3Client.class), eq(bucket), eq(requestJson));
        assertThat(result, is("{\"status\":\"Ok\",\"objects\":[\"reports/file.json\"]}"));
    }

    @Test
    public void testExecute_operationCaseInsensitive() {
        String bucket = "test-bucket";
        String requestJson = "{\"prefix\":\"\"}";
        when(lister.list(any(S3Client.class), eq(bucket), eq(requestJson)))
                .thenReturn("{\"status\":\"Ok\",\"objects\":[]}");

        basicS3Client.execute(bucket, "LIST", requestJson);
        basicS3Client.execute(bucket, "List", requestJson);

        verify(lister, times(2)).list(any(S3Client.class), eq(bucket), eq(requestJson));
    }

    @Test
    public void testExecute_unsupportedOperation_throwsException() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Unsupported S3 operation: 'delete'");

        basicS3Client.execute("test-bucket", "delete", "{}");
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = BasicS3Client.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
