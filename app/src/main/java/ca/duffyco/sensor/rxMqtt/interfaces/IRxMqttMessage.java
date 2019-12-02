package ca.duffyco.sensor.rxMqtt.interfaces;

/**
 * Created by xudshen@hotmail.com on 14-7-21.
 */
public interface IRxMqttMessage {
    public String getTopic();

    public String getMessage();

    public int getQos();
}
