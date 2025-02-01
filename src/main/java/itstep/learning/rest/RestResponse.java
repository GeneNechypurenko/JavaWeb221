package itstep.learning.rest;
import java.util.*;

public class RestResponse {

    private int status;
    private String message;
    private String resourceUrl;
    private Map<String, String> metadata;
    private long cacheTime;
    private Object data;

    public String getResourceUrl() {
        return resourceUrl;
    }

    public RestResponse setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        return this;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public RestResponse setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public RestResponse setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RestResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public RestResponse setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RestResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}