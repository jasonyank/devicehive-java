package com.devicehive.rest;

import com.devicehive.rest.api.DeviceCommandApi;
import com.devicehive.rest.model.CommandInsert;
import com.devicehive.rest.model.DeviceCommand;
import com.devicehive.rest.model.DeviceCommandWrapper;
import com.devicehive.rest.model.JsonStringWrapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeviceCommandApiTest extends Helper {

    private static final String COMMAND_NAME = "TEST COMMAND";
    private static final String TEST_PROP = "testProp";
    private static final String TEST_VALUE = "testValue";
    private static final String UPDATED_COMMAND_NAME = "UPDATED COMMAND";

    //DeviceCommandApi
    private DeviceCommandWrapper getCommandWrapper() {
        DeviceCommandWrapper deviceCommandWrapper = new DeviceCommandWrapper();
        deviceCommandWrapper.setCommand(COMMAND_NAME);

        JSONObject data = new JSONObject();
        data.put(TEST_PROP, TEST_VALUE);
        JsonStringWrapper jsonStringWrapper = new JsonStringWrapper();
        jsonStringWrapper.setJsonString(new Gson().toJson(data));
        deviceCommandWrapper.setParameters(jsonStringWrapper);
        return deviceCommandWrapper;
    }

    @Test
    public void insertCommand() throws IOException {
        String deviceId = UUID.randomUUID().toString();
        boolean authenticated = authenticate();
        boolean deviceCreated = createDevice(deviceId);
        Assert.assertTrue(authenticated && deviceCreated);

        DeviceCommandWrapper deviceCommandWrapper = getCommandWrapper();
        DeviceCommandApi commandApi = client.createService(DeviceCommandApi.class);

        Response<CommandInsert> response = commandApi.insert(deviceId, deviceCommandWrapper).execute();
        Assert.assertTrue(response.isSuccessful());
        Assert.assertTrue(deleteDevices(deviceId));
    }

    @Test
    public void getCommand() throws IOException {
        String deviceId = UUID.randomUUID().toString();
        boolean authenticated = authenticate();
        boolean deviceCreated = createDevice(deviceId);
        Assert.assertTrue(authenticated && deviceCreated);


        DeviceCommandWrapper deviceCommandWrapper = getCommandWrapper();

        DeviceCommandApi commandApi = client.createService(DeviceCommandApi.class);

        Response<CommandInsert> response = commandApi.insert(deviceId, deviceCommandWrapper).execute();

        Assert.assertTrue(response.body() != null);
        CommandInsert command = response.body();
        assert command != null;
        String commandId = String.valueOf(command.getCommandId());
        Assert.assertTrue(commandId != null);

        Response<DeviceCommand> getResponse = commandApi.get(deviceId, commandId).execute();
        DeviceCommand getCommand = getResponse.body();
        Assert.assertTrue(getResponse.isSuccessful());
        assert getCommand != null;
        Assert.assertTrue(getCommand.getId().equals(command.getCommandId()));
        Assert.assertTrue(deleteDevices(deviceId));
    }


    @Test
    public void pollCommand() throws IOException {
        String deviceId = UUID.randomUUID().toString();
        boolean authenticated = authenticate();
        boolean deviceCreated = createDevice(deviceId);
        Assert.assertTrue(authenticated && deviceCreated);

        DeviceCommandApi commandApi = client.createService(DeviceCommandApi.class);

        DeviceCommandWrapper deviceCommandWrapper = getCommandWrapper();
        DateTime currentTimestamp = DateTime.now();
        for (int i = 0; i < 5; i++) {
            commandApi.insert(deviceId, deviceCommandWrapper).execute();
        }
        String timestamp = currentTimestamp.withMillis(0).toString();
        Response<List<DeviceCommand>> pollResponse = commandApi.poll(deviceId, COMMAND_NAME,
                timestamp, 30L, 10).execute();
        Assert.assertTrue(pollResponse.isSuccessful());
        Assert.assertTrue(pollResponse.body().size() == 5);
        Assert.assertTrue(deleteDevices(deviceId));
    }

    @Test
    public void pollManyCommand() throws IOException {
        String deviceId1 = UUID.randomUUID().toString();
        String deviceId2 = UUID.randomUUID().toString();

        boolean authenticated = authenticate();
        boolean firstDeviceCreated = createDevice(deviceId1);
        boolean secondDeviceCreated = createDevice(deviceId2);
        Assert.assertTrue(authenticated && firstDeviceCreated && secondDeviceCreated);

        DeviceCommandApi commandApi = client.createService(DeviceCommandApi.class);

        DeviceCommandWrapper deviceCommandWrapper = getCommandWrapper();
        DateTime currentTimestamp = DateTime.now();
        String timestamp = currentTimestamp.withMillis(0).toString();
        for (int i = 0; i < 5; i++) {
            commandApi.insert(deviceId1, deviceCommandWrapper).execute();
            commandApi.insert(deviceId2, deviceCommandWrapper).execute();
        }


        List<String> list = new ArrayList<>();
        list.add(deviceId1);
        list.add(deviceId2);
        String deviceIds = StringUtils.join(list, ",");
        Response<List<DeviceCommand>> pollResponse = commandApi.pollMany(
                deviceIds,
                COMMAND_NAME,
                timestamp, 30L, 20).execute();
        Assert.assertTrue(pollResponse.isSuccessful());
        Assert.assertTrue(pollResponse.body().size() == 10);
        Assert.assertTrue(deleteDevices(deviceId1, deviceId2));
    }

    @Test
    public void queryCommand() throws IOException {
        String deviceId = UUID.randomUUID().toString();
        boolean authenticated = authenticate();
        boolean deviceCreated = createDevice(deviceId);
        Assert.assertTrue(authenticated && deviceCreated);

        DeviceCommandApi commandApi = client.createService(DeviceCommandApi.class);
        DeviceCommandWrapper deviceCommandWrapper = getCommandWrapper();
        DateTime currentTimestamp = DateTime.now();
        for (int i = 1; i <= 5; i++) {
            commandApi.insert(deviceId, deviceCommandWrapper).execute();
        }
        String current = currentTimestamp.withMillis(0).toString();
        String endTimestamp = currentTimestamp.plusMinutes(2).toString();
        Response<List<DeviceCommand>> response = commandApi.query(deviceId, current, endTimestamp, COMMAND_NAME,
                null, null, null, 10, 0).execute();
        Assert.assertTrue(response.isSuccessful());
        Assert.assertTrue(response.body().size() == 5);
        Assert.assertTrue(deleteDevices(deviceId));
    }

    @Test
    public void updateCommand() throws IOException {
        String deviceId = UUID.randomUUID().toString();
        boolean authenticated = authenticate();
        boolean deviceCreated = createDevice(deviceId);
        Assert.assertTrue(authenticated && deviceCreated);


        DeviceCommandApi commandApi = client.createService(DeviceCommandApi.class);
        DeviceCommandWrapper wrapper = getCommandWrapper();
        Response<CommandInsert> response = commandApi.insert(deviceId, wrapper).execute();
        Assert.assertTrue(response.isSuccessful());
        Response<DeviceCommand> getResponse = commandApi.get(deviceId, String.valueOf(response.body().getCommandId())).execute();

        long id = response.body().getCommandId();

        wrapper.setResult(getResponse.body().getResult());
        wrapper.setCommand(UPDATED_COMMAND_NAME);
        wrapper.setLifetime(getResponse.body().getLifetime());
        Response<Void> updatedResponse = commandApi.update(deviceId, id, wrapper).execute();
        Assert.assertTrue(updatedResponse.isSuccessful());
        Assert.assertTrue(deleteDevices(deviceId));
    }

    @Test
    public void waitCommand() throws IOException {
        final String deviceId = UUID.randomUUID().toString();
        boolean authenticated = authenticate();
        boolean deviceCreated = createDevice(deviceId);
        Assert.assertTrue(authenticated && deviceCreated);


        final DeviceCommandApi commandApi = client.createService(DeviceCommandApi.class);
        final DeviceCommandWrapper wrapper = getCommandWrapper();

        final Response<CommandInsert> response = commandApi.insert(deviceId, wrapper).execute();
        final Response<DeviceCommand> getResponse = commandApi.get(deviceId, String.valueOf(response.body().getCommandId())).execute();
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    commandApi.update(deviceId, getResponse.body().getId(), wrapper).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        Response<DeviceCommand> updatedResponse = commandApi.wait(deviceId, getResponse.body().getId(), 60L).execute();
        Assert.assertTrue(response.isSuccessful());
        Assert.assertTrue(updatedResponse.isSuccessful());
        Assert.assertTrue(deleteDevices(deviceId));
    }
}
