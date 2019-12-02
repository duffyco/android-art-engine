package ca.duffyco.sensor.rxMqtt.interfaces;

import org.eclipse.paho.client.mqttv3.MqttClientPersistence;

import ca.duffyco.sensor.rxMqtt.enums.ClientType;
import ca.duffyco.sensor.rxMqtt.exceptions.RxMqttException;

/**
 * Created by xudshen@hotmail.com on 14-7-21.
 */
public interface IRxMqttClientFactory {
    public IRxMqttClient create(String host, Integer port, String clientId, Boolean useSSL,
                                ClientType type, MqttClientPersistence persistence)
            throws RxMqttException;
}
