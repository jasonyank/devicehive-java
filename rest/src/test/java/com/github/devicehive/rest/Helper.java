/*
 *
 *
 *   Helper.java
 *
 *   Copyright (C) 2017 DataArt
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.github.devicehive.rest;

import com.github.devicehive.rest.api.DeviceApi;
import com.github.devicehive.rest.api.NetworkApi;
import com.github.devicehive.rest.api.UserApi;
import com.github.devicehive.rest.model.*;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;


class Helper {

    private static final String LOGIN = "***REMOVED***";
    private static final String PASSWORD = "***REMOVED***";
    private static final String URL = "***REMOVED***/";

    ApiClient client = new ApiClient(URL);

    boolean authenticate() throws IOException {
        com.github.devicehive.rest.api.JwtTokenApi api = client.createService(com.github.devicehive.rest.api.JwtTokenApi.class);
        com.github.devicehive.rest.model.JwtRequest requestBody = new com.github.devicehive.rest.model.JwtRequest();
        requestBody.setLogin(LOGIN);
        requestBody.setPassword(PASSWORD);
        Response<JwtToken> response = api.login(requestBody).execute();
        if (response.isSuccessful()) {
            client.addAuthorization(ApiClient.AUTH_API_KEY, com.github.devicehive.rest.auth.ApiKeyAuth.newInstance(response.body().getAccessToken()));
        }
        return response.isSuccessful();
    }


    boolean createDevice(@Nonnull String deviceId) throws IOException {
        DeviceUpdate device = new DeviceUpdate();
        device.setName(com.github.devicehive.rest.utils.Const.NAME);
        device.setId(deviceId);
        DeviceApi deviceApi = client.createService(DeviceApi.class);
        com.github.devicehive.rest.api.NetworkApi networkApi = client.createService(com.github.devicehive.rest.api.NetworkApi.class);
        Response<List<Network>> networkResponse = networkApi.list(null, null, null,
                null, null, null).execute();
        List<Network> networks = networkResponse.body();

        if (networks != null && !networks.isEmpty()) {
            device.setNetworkId(networks.get(0).getId());
            Response<Void> response = deviceApi.register(device, deviceId).execute();
            return response.isSuccessful();
        } else {
            return false;
        }
    }

    NetworkId createNetwork(@Nonnull String networkName) throws IOException {
        NetworkApi networkApi = client.createService(NetworkApi.class);
        NetworkUpdate networkUpdate = new NetworkUpdate();
        networkUpdate.setName(networkName);

        Response<NetworkId> insertResponse = networkApi.insert(networkUpdate).execute();
        return insertResponse.body();
    }

    boolean deleteDevices(String... ids) throws IOException {
        int count = 0;
        DeviceApi api = client.createService(DeviceApi.class);
        for (String id : ids) {
            if (api.delete(id).execute().isSuccessful()) {
                count++;
            } else {
                return false;
            }
        }
        return count == ids.length;
    }

    boolean deleteNetworks(Long... ids) throws IOException {
        int count = 0;
        com.github.devicehive.rest.api.NetworkApi networkApi = client.createService(com.github.devicehive.rest.api.NetworkApi.class);
        for (Long id : ids) {
            if (networkApi.delete(id).execute().isSuccessful()) {
                count++;
            } else {
                return false;
            }
        }
        return count == ids.length;
    }

    boolean deleteUsers(Long... ids) throws IOException {
        int count = 0;
        UserApi userApi = client.createService(UserApi.class);
        for (Long id : ids) {
            if (userApi.deleteUser(id).execute().isSuccessful()) {
                count++;
            } else {
                return false;
            }
        }
        return count == ids.length;
    }
}
