package com.devicehive.client.impl.context;


import com.devicehive.client.HiveMessageHandler;
import com.devicehive.client.impl.json.strategies.JsonPolicyDef;
import com.devicehive.client.impl.rest.HiveRestConnector;
import com.devicehive.client.model.*;
import com.devicehive.client.model.exceptions.HiveException;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Type;
import java.net.URI;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class RestAgent extends AbstractHiveAgent {

    private static final int TIMEOUT = 60;
    protected final URI restUri;
    private final ExecutorService subscriptionExecutor = Executors.newFixedThreadPool(50);
    private ConcurrentMap<String, Future> commandSubscriptionsResults = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Future> notificationSubscriptionResults = new ConcurrentHashMap<>();
    private HiveRestConnector restConnector;

    public RestAgent(URI restUri) {
        this.restUri = restUri;
    }

    public synchronized HiveRestConnector getRestConnector() {
        return restConnector;
    }

    @Override
    public synchronized void authenticate(HivePrincipal hivePrincipal) throws HiveException {
        super.authenticate(hivePrincipal);
        if (restConnector != null) {
            restConnector.setHivePrincipal(hivePrincipal);
        }
    }

    @Override
    protected void beforeConnect() throws HiveException {

    }

    @Override
    protected void doConnect() throws HiveException {
        this.restConnector = new HiveRestConnector(restUri);
    }

    @Override
    protected void afterConnect() throws HiveException {
        restConnector.setHivePrincipal(getHivePrincipal());
    }


    @Override
    protected void beforeDisconnect() {
        MoreExecutors.shutdownAndAwaitTermination(subscriptionExecutor, 1, TimeUnit.MINUTES);
        commandSubscriptionsResults.clear();
        notificationSubscriptionResults.clear();
    }

    @Override
    protected void doDisconnect() {
        this.restConnector.close();
    }

    @Override
    protected void afterDisconnect() {
    }

    @Override
    public synchronized void close()  {
        super.close();
    }

    public synchronized String subscribeForCommands(final SubscriptionFilter newFilter,
                                                    final HiveMessageHandler<DeviceCommand> handler)
            throws HiveException {

        final String subscriptionIdValue = UUID.randomUUID().toString();
        addCommandsSubscription(subscriptionIdValue, new SubscriptionDescriptor<>(handler, newFilter));


        RestSubscription sub = new RestSubscription() {

            @Override
            protected void execute() throws HiveException {
                Map<String, Object> params = new HashMap<>();
                params.put(Constants.WAIT_TIMEOUT_PARAM, String.valueOf(TIMEOUT));
                if (newFilter != null) {
                    params.put(Constants.TIMESTAMP, newFilter.getTimestamp());
                    params.put(Constants.NAMES, StringUtils.join(newFilter.getNames(), Constants.SEPARATOR));
                    params.put(Constants.DEVICE_GUIDS, StringUtils.join(newFilter.getUuids(), Constants.SEPARATOR));
                }
                Type responseType = new TypeToken<List<CommandPollManyResponse>>() {
                }.getType();
                List<CommandPollManyResponse> responses =
                        getRestConnector().execute("/device/command/poll", HttpMethod.GET, null,
                                params, responseType, JsonPolicyDef.Policy.COMMAND_LISTED);
                for (CommandPollManyResponse response : responses) {
                    SubscriptionDescriptor<DeviceCommand> descriptor =
                            getCommandsSubscriptionDescriptor(subscriptionIdValue);
                    descriptor.handleMessage(response.getCommand());
                }
            }
        };
        Future commandsSubscription = subscriptionExecutor.submit(sub);
        commandSubscriptionsResults.put(subscriptionIdValue, commandsSubscription);
        return subscriptionIdValue;
    }


    /**
     * Remove command subscription.
     */
    public synchronized void unsubscribeFromCommands(String subscriptionId) throws HiveException {
        Future commandsSubscription = commandSubscriptionsResults.remove(subscriptionId);
        if (commandsSubscription != null) {
            commandsSubscription.cancel(true);
        }
        super.removeCommandsSubscription(subscriptionId);
    }

    public synchronized void subscribeForCommandUpdates(final Long commandId,
                                                        final String guid,
                                                        final HiveMessageHandler<DeviceCommand> handler)
            throws HiveException {
        RestSubscription sub = new RestSubscription() {
            @Override
            protected void execute() throws HiveException {
                DeviceCommand result = null;
                Map<String, Object> params = new HashMap<>();
                params.put(Constants.WAIT_TIMEOUT_PARAM, String.valueOf(TIMEOUT));
                Type responseType = new TypeToken<DeviceCommand>() {
                }.getType();
                while (result == null && !Thread.currentThread().isInterrupted()) {
                    result = getRestConnector().execute(
                            String.format("/device/%s/command/%s/poll", guid, commandId),
                            HttpMethod.GET,
                            null,
                            params,
                            responseType,
                            JsonPolicyDef.Policy.COMMAND_TO_DEVICE);
                }
                handler.handle(result);
            }
        };
        subscriptionExecutor.submit(sub);
    }


    public synchronized String subscribeForNotifications(final SubscriptionFilter newFilter,
                                                         final HiveMessageHandler<DeviceNotification> handler) throws HiveException {
        final String subscriptionIdValue = UUID.randomUUID().toString();
        addNotificationsSubscription(subscriptionIdValue, new SubscriptionDescriptor<>(handler, newFilter));
        RestSubscription sub = new RestSubscription() {
            @Override
            protected void execute() throws HiveException {
                Map<String, Object> params = new HashMap<>();
                params.put(Constants.WAIT_TIMEOUT_PARAM, String.valueOf(TIMEOUT));
                if (newFilter != null) {
                    params.put(Constants.TIMESTAMP, newFilter.getTimestamp());
                    params.put(Constants.NAMES, StringUtils.join(newFilter.getNames(), Constants.SEPARATOR));
                    params.put(Constants.DEVICE_GUIDS, StringUtils.join(newFilter.getUuids(), Constants.SEPARATOR));
                }
                Type responseType = new TypeToken<List<NotificationPollManyResponse>>() {
                }.getType();
                List<NotificationPollManyResponse> responses = getRestConnector().execute(
                        "/device/notification/poll",
                        HttpMethod.GET,
                        null,
                        params,
                        responseType,
                        JsonPolicyDef.Policy.NOTIFICATION_TO_CLIENT);
                for (NotificationPollManyResponse response : responses) {
                    SubscriptionDescriptor<DeviceNotification> descriptor = getNotificationsSubscriptionDescriptor(
                            subscriptionIdValue);
                    descriptor.handleMessage(response.getNotification());
                }
            }
        };
        Future notificationsSubscription = subscriptionExecutor.submit(sub);
        notificationSubscriptionResults.put(subscriptionIdValue, notificationsSubscription);
        return subscriptionIdValue;
    }

    /**
     * Remove command subscription for all available commands.
     */
    public synchronized void unsubscribeFromNotifications(String subscriptionId) throws HiveException {
        Future notificationsSubscription = notificationSubscriptionResults.remove(subscriptionId);
        if (notificationsSubscription != null) {
            notificationsSubscription.cancel(true);
        }
        super.removeNotificationsSubscription(subscriptionId);
    }

    /**
     * Get API info from server
     *
     * @return API info
     */
    public ApiInfo getInfo() throws HiveException {
        return getRestConnector().execute("/info", HttpMethod.GET, null, ApiInfo.class, null);
    }

    public Timestamp getServerTimestamp() throws HiveException {
        return getInfo().getServerTimestamp();
    }

    public String getServerApiVersion() throws HiveException {
        return getInfo().getApiVersion();
    }

    private abstract static class RestSubscription<T> implements Runnable {
        private static Logger logger = LoggerFactory.getLogger(RestSubscription.class);

        protected abstract void execute() throws HiveException;

        @Override
        public final void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    execute();
                } catch (Throwable e) {
                    logger.error("Error processing subscription", e);
                }
            }
        }
    }
}
