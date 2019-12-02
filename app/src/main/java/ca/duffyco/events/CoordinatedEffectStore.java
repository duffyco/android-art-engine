package ca.duffyco.events;

import java.util.Vector;

import ca.duffyco.RenderEngine.Effect;

/**
 * Created by lidia on 3/19/2016.
 */
public abstract class CoordinatedEffectStore implements EventStore
{
    protected Vector<Effect> observers = new Vector<Effect>();

    public CoordinatedEffectStore()
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
}
