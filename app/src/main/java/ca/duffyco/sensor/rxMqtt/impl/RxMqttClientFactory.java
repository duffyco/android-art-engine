package ca.duffyco.sensor.rxMqtt.impl;

import org.eclipse.paho.client.mqttv3.MqttClientPersistence;

import ca.duffyco.sensor.rxMqtt.constants.Constants;
import ca.duffyco.sensor.rxMqtt.enums.ClientType;
import ca.duffyco.sensor.rxMqtt.exceptions.RxMqttException;
import ca.duffyco.sensor.rxMqtt.interfaces.IRxMqttClient;
import ca.duffyco.sensor.rxMqtt.interfaces.IRxMqttClientFactory;

/**
 * Created by xudshen@hotmail.com on 14-7-21.
 */
public class RxMqttClientFactory implements IRxMqttClientFactory {
    @Override
    public IRxMqttClient create(String host, Integer port, String clientId, Boolean useSSL,
                                ClientType type, MqttClientPersistence persistence)
            throws RxMqttException {
        IRxMqttClient client = null;
        String brokerUrl = getBrokerUrl(host, port, useSSL);
        switch (type) {
            case Async: {
                client = new RxMqttAsyncClient(brokerUrl, clientId, persistence);
                break;
            }
            case Wait: {
                break;
            }
        }
        return client;
    }

    private String getBrokerUrl(String host, int port, Boolean useSSL) {
        return String.format("%s://%s:%d", useSSL ? Constants.SSL : Constants.TCP, host, port);
    }
}
