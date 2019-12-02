package ca.duffyco.sensor.rxMqtt.exceptions;

import ca.duffyco.sensor.rxMqtt.enums.RxMqttExceptionType;

/**
 * Created by xudshen@hotmail.com on 14-7-21.
 */
public class RxMqttException extends Throwable {
    private RxMqttExceptionType type;

    public RxMqttException() {
    }

    public RxMqttException(RxMqttExceptionType type) {
        this.setType(type);
    }

    public RxMqttException(RxMqttExceptionType type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public RxMqttExceptionType getType() {
        return type;
    }

    public void setType(RxMqttExceptionType type) {
        this.type = type;
    }
}
