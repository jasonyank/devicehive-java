package com.devicehive.client.websocket.model;

/**
 * Wrapper for JSON objects. Represents JSON object as a string
 */
public class JsonStringWrapper implements HiveEntity {

    private static final long serialVersionUID = -152849186108390497L;
    private String jsonString;

    public JsonStringWrapper() {
    }

    public JsonStringWrapper(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof com.devicehive.client.model.JsonStringWrapper)) {
            return false;
        }

        return jsonString.equals(((com.devicehive.client.model.JsonStringWrapper) obj).getJsonString());
    }

    @Override
    public int hashCode() {
        return jsonString != null ? jsonString.hashCode() : 0;
    }

    @Override
    public String toString() {
        return jsonString;
    }
}
