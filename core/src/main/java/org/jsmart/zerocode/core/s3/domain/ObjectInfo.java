package org.jsmart.zerocode.core.s3.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metadata for a single S3 object returned in a list response.
 *
 * <p>Example JSON:
 * <pre>
 * {
 *   "key": "reports/result.json",
 *   "size": 1024,
 *   "lastModified": "2024-03-15T10:30:00Z",
 *   "storageClass": "STANDARD"
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectInfo {

    @JsonProperty("key")
    private String key;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("lastModified")
    private String lastModified;

    @JsonProperty("storageClass")
    private String storageClass;

    public ObjectInfo() {
    }

    public ObjectInfo(String key, Long size, String lastModified, String storageClass) {
        this.key = key;
        this.size = size;
        this.lastModified = lastModified;
        this.storageClass = storageClass;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    @Override
    public String toString() {
        return "ObjectInfo{" +
                "key='" + key + '\'' +
                ", size=" + size +
                ", lastModified='" + lastModified + '\'' +
                ", storageClass='" + storageClass + '\'' +
                '}';
    }
}
