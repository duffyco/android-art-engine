package ca.duffyco.events;

import java.util.HashMap;

import ca.duffyco.RenderEngine.Effect;

/**
 * Created by lidia on 3/13/2016.
 */
public class EventManager
{
    private static EventManager manager = new EventManager();

    private HashMap< String, EventStore> observers = new HashMap<>();

    public EventStore getEventStore( String sensorName )
    {
        return observers.get( sensorName );
    }

    public static EventManager getInstance() {
        return manager;
    }

    public static void dispatch( Event<?> e )
    {
        EventStore store = manager.observers.get( e.sensorName );

        if( store != null )
            store.dispatch( e );
    }
    public void subscribe( String sensorName, EventStore e )
    {
        observers.put( sensorName, e );
    }

    public void unsubscribe( String sensorName, Effect e )
    {
        if( observers.get( sensorName ) == null )
            return;

        observers.remove( sensorName );
    }

}
