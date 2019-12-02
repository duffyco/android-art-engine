
package ca.duffyco.events;

import java.util.Vector;

import ca.duffyco.RenderEngine.Effect;

/**
 * Created by lidia on 3/19/2016.
 */
public class SequentialEffectStore implements EventStore
{
    protected Vector<Effect> observers = new Vector<Effect>();

    public SequentialEffectStore()
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

    private int currentLayer = 0;

    @Override
    public void dispatch(Event<?> in) {
        Event<Integer> e = (Event<Integer>) in;

        observers.elementAt( currentLayer ).onNext( e.eventMessage );

        if( observers.elementAt( currentLayer ).isComplete() )
            currentLayer++;

        observers.elementAt( currentLayer ).onNext( e.eventMessage );
    }
}

