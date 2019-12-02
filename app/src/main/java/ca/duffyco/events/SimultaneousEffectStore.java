package ca.duffyco.events;

import java.util.Iterator;
import java.util.Vector;

import ca.duffyco.RenderEngine.Effect;

/**
 * Created by lidia on 3/19/2016.
 */
public class SimultaneousEffectStore implements EventStore
{
    protected Vector<Effect> observers = new Vector<Effect>();

    public SimultaneousEffectStore()
    {
    }

    public void addObserver( Effect e )
    {
        this.observers.add( e );
    }

    public void removeObserver( Effect observer )
    {
        this.observers.remove( this.observers.indexOf( observer ) );
    }

    private int lastActiveLayer = Effect.OFF;

    @Override
    public void dispatch(Event<?> in) {
        Event<Integer> e = (Event<Integer>) in;

        Iterator<Effect> iter = this.observers.iterator();

        while (iter.hasNext())
            iter.next().onNext(e.eventMessage);
    }
}
