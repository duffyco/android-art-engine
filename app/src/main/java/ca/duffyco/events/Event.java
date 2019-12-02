package ca.duffyco.events;

/**
 * Created by lidia on 3/13/2016.
 */

public class Event<T>
{
    public enum EventType {
        READ,
        WRITE,
    };

    public Event(int id, String name, EventType e, T msg )
    {
        this.id = id;
        sensorName = name;
        eventType = e;
        eventMessage = msg;
    }

    public int id;
    public String sensorName;
    public EventType eventType;
    public T eventMessage;
}
