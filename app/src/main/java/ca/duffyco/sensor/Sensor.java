package ca.duffyco.sensor;

import org.eclipse.paho.client.mqttv3.IMqttToken;

import ca.duffyco.sensor.rxMqtt.impl.RxMqttMessage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by lidia on 2/6/2016.
 */
public abstract class Sensor<T> {
    public int id;
    public String name;

    public abstract Subscription connect(final String topic);

    public abstract Observable<T> subscribeTo();

    public abstract Observable<IMqttToken> disconnect();
}