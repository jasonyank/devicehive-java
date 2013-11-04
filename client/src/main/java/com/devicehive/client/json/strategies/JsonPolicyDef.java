package com.devicehive.client.json.strategies;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPolicyDef {

    Policy[] value();

    public static enum Policy {
        ACCESS_KEY_LISTED,
        ACCESS_KEY_PUBLISHED,
        ACCESS_KEY_SUBMITTED,
        DEVICE_PUBLISHED,
        DEVICE_SUBMITTED,
        COMMAND_TO_CLIENT,
        COMMAND_TO_DEVICE,
        COMMAND_LISTED,
        POST_COMMAND_TO_DEVICE,
        COMMAND_FROM_CLIENT,
        COMMAND_UPDATE_FROM_DEVICE,
        COMMAND_UPDATE_TO_CLIENT,
        REST_COMMAND_UPDATE_FROM_DEVICE,
        NOTIFICATION_FROM_DEVICE,
        NOTIFICATION_TO_DEVICE,
        NOTIFICATION_TO_CLIENT,
        DEVICE_EQUIPMENT_SUBMITTED,
        EQUIPMENT_SUBMITTED,
        USER_PUBLISHED,
        USER_SUBMITTED,
        USERS_LISTED,
        USER_UPDATE,
        NETWORK_PUBLISHED,
        NETWORKS_LISTED,
        NETWORK_SUBMITTED,
        NETWORK_UPDATE,
        DEVICECLASS_LISTED,
        DEVICECLASS_PUBLISHED,
        DEVICECLASS_SUBMITTED,
        EQUIPMENTCLASS_PUBLISHED,
        EQUIPMENTCLASS_SUBMITTED,
        OAUTH_CLIENT_LISTED,
        OAUTH_CLIENT_PUBLISHED,
        OAUTH_CLIENT_SUBMITTED,
        OAUTH_GRANT_LISTED,
        OAUTH_GRANT_SUBMITTED_TOKEN,
        OAUTH_GRANT_SUBMITTED_CODE,
        OAUTH_GRANT_PUBLISHED
    }
}
