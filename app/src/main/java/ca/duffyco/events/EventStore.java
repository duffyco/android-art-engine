package ca.duffyco.events;

import ca.duffyco.RenderEngine.Effect;

/**
 * Created by lidia on 3/13/2016.
 */
public interface EventStore
{
    public abstract void dispatch( Event<?> event );
    public void addObserver( Effect e );
    public void removeObserver( Effect e );
}
