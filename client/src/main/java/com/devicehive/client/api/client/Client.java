package com.devicehive.client.api.client;


import com.devicehive.client.api.AuthenticationService;
import com.devicehive.client.context.HiveContext;
import com.devicehive.client.context.HivePrincipal;
import com.devicehive.client.model.ApiInfo;
import com.devicehive.client.model.Transport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;

public class Client implements HiveClient {

    private static final String CLIENT_ENDPOINT_PATH = "/client";
    private final HiveContext hiveContext;

    public Client(URI uri, URI websocket) {
        String ws = StringUtils.removeEnd(websocket.toString(), "/");
        hiveContext = new HiveContext(Transport.AUTO, uri, URI.create(ws + CLIENT_ENDPOINT_PATH));
    }

    public Client(URI uri, URI websocket, Transport transport) {
        String ws = StringUtils.removeEnd(websocket.toString(), "/");
        hiveContext = new HiveContext(transport, uri, URI.create(ws + CLIENT_ENDPOINT_PATH));
    }

    public ApiInfo getInfo() {
        return hiveContext.getInfo();
    }

    public void authenticate(String login, String password) {
        if (hiveContext.useSockets()) {
            AuthenticationService.authenticateClient(login, password, hiveContext);
        }
        hiveContext.setHivePrincipal(HivePrincipal.createUser(login, password));
    }

    public void authenticate(String accessKey) {
        if (hiveContext.useSockets()) {
            AuthenticationService.authenticateKey(accessKey, hiveContext);
        }
        hiveContext.setHivePrincipal(HivePrincipal.createAccessKey(accessKey));

    }

    public AccessKeyController getAccessKeyController() {
        return new AccessKeyControllerImpl(hiveContext);
    }

    public CommandsController getCommandsController() {
        return new CommandsControllerImpl(hiveContext);
    }

    public DeviceController getDeviceController() {
        return new DeviceControllerImpl(hiveContext);
    }

    public NetworkController getNetworkController() {
        return new NetworkControllerImpl(hiveContext);
    }

    public NotificationsController getNotificationsController() {
        return new NotificationsControllerImpl(hiveContext);
    }

    public UserController getUserController() {
        return new UserControllerImpl(hiveContext);
    }

    public OAuthClientController getOAuthClientController() {
        return new OAuthClientControllerImpl(hiveContext);
    }

    public OAuthGrantController getOAuthGrantController() {
        return new OAuthGrantControllerImpl(hiveContext);
    }

    public OAuthTokenController getOAuthTokenController() {
        return new OAuthTokenControllerImpl(hiveContext);
    }

    @Override
    public void close() throws IOException {
        hiveContext.close();
    }

}
