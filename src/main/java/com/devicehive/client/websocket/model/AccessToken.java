package com.devicehive.client.websocket.model;


import com.google.gson.annotations.SerializedName;

/**
 * Represents access token For more details see <a href="http://tools.ietf.org/html/rfc6749">The OAuth 2.0 Authorization
 * Framework</a>
 */
public class AccessToken implements HiveEntity {

    private static final long serialVersionUID = 663053837130392591L;
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Long expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }


}
