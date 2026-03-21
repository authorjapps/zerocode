package org.jsmart.zerocode.core.s3.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the request payload for an S3 step operation.
 *
 * <p>Upload example:
 * <pre>
 * {
 *   "key": "reports/result.json",
 *   "file": "test-data/result.json"
 * }
 * </pre>
 *
 * <p>Download example:
 * <pre>
 * {
 *   "key": "reports/result.json",
 *   "localPath": "/tmp/downloaded.json"
 * }
 * </pre>
 *
 * <p>List example (all objects with prefix, auto-paginated):
 * <pre>
 * {
 *   "prefix": "reports/"
 * }
 * </pre>
 *
 * <p>List example (single page of up to 10, with continuation):
 * <pre>
 * {
 *   "prefix": "reports/",
 *   "maxKeys": 10,
 *   "continuationToken": "1ueGcxLPRx1Tr/XYExHnhbYLgveDs2J/wm36Hy4vbOwM="
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class S3Request {

    @JsonProperty("key")
    private String key;

    @JsonProperty("file")
    @JsonAlias("filePath")
    private String file;

    @JsonProperty("localPath")
    @JsonAlias("saveAs")
    private String localPath;

    @JsonProperty("prefix")
    @JsonAlias("folder")
    private String prefix;

    /**
     * Maximum number of objects to return in a single list response.
     * When absent (null), S3Lister auto-paginates through all pages and
     * returns every object in one response.
     */
    @JsonProperty("maxKeys")
    private Integer maxKeys;

    /**
     * Opaque token returned as {@code nextContinuationToken} in a prior
     * truncated list response. Pass it here to fetch the next page.
     */
    @JsonProperty("continuationToken")
    private String continuationToken;

    public S3Request() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    @Override
    public String toString() {
        return "S3Request{" +
                "key='" + key + '\'' +
                ", file='" + file + '\'' +
                ", localPath='" + localPath + '\'' +
                ", prefix='" + prefix + '\'' +
                ", maxKeys=" + maxKeys +
                ", continuationToken='" + continuationToken + '\'' +
                '}';
    }
}
