package org.jsmart.zerocode.core.s3.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the response payload returned after an S3 step operation.
 *
 * <p>Upload/Download response:
 * <pre>
 * {
 *   "status": "Ok",
 *   "s3Url": "s3://my-bucket/reports/result.json"
 * }
 * </pre>
 *
 * <p>List response (single page, not truncated):
 * <pre>
 * {
 *   "status": "Ok",
 *   "objects": [
 *     { "key": "reports/a.json", "size": 1024, "lastModified": "2024-03-15T10:30:00Z", "storageClass": "STANDARD" },
 *     { "key": "reports/b.json", "size": 2048, "lastModified": "2024-03-16T08:00:00Z", "storageClass": "STANDARD" }
 *   ],
 *   "truncated": false
 * }
 * </pre>
 *
 * <p>List response (truncated — use nextContinuationToken in the next step's continuationToken):
 * <pre>
 * {
 *   "status": "Ok",
 *   "objects": [ ... ],
 *   "truncated": true,
 *   "nextContinuationToken": "1ueGcxLPRx1Tr/XYExHnhbYLgveDs2J/wm36Hy4vbOwM="
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class S3Response {

    @JsonProperty("status")
    private String status;

    @JsonProperty("s3Url")
    private String s3Url;

    @JsonProperty("objects")
    private List<ObjectInfo> objects;

    @JsonProperty("truncated")
    private Boolean truncated;

    @JsonProperty("nextContinuationToken")
    private String nextContinuationToken;

    public S3Response() {
    }

    public S3Response(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public List<ObjectInfo> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectInfo> objects) {
        this.objects = objects;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public String getNextContinuationToken() {
        return nextContinuationToken;
    }

    public void setNextContinuationToken(String nextContinuationToken) {
        this.nextContinuationToken = nextContinuationToken;
    }

    @Override
    public String toString() {
        return "S3Response{" +
                "status='" + status + '\'' +
                ", s3Url='" + s3Url + '\'' +
                ", objects=" + objects +
                ", truncated=" + truncated +
                ", nextContinuationToken='" + nextContinuationToken + '\'' +
                '}';
    }
}
