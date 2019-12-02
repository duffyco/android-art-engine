package ca.duffyco.RenderEngine;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by jon on 10/19/2016.
 */

abstract public class CoordinatedEventGroup<T> extends Effect<T>
{
    protected Vector<Effect> effects = new Vector<Effect>();
    protected Effect<T> currentEffect = null;

    public CoordinatedEventGroup() {
    }

    public boolean isComplete() {
        for( Effect e : effects ) {
            if( !e.isComplete() ) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        for( Effect e : effects ) {
            e.reset();
            e.setActive( false );
        }
        //Remove as part of @TODO
        currentEffect = null;
    }


    public void subscribe( Effect<T> newEffect )
    {
        effects.add(newEffect);
    }

    public void unsubscribe( Effect<T> effect )
    {
        effects.remove(effects.indexOf(effect));
    }

    public void addTexture( Texture t )
    {
    }

    public void onNext( T data )
    {
        if( !isActive )
          setActive( true );

        /*
         *@TODO: Adjusted to turn on the next layer if complete
         */

        for( int i=0; i < effects.size(); i++ )
        {
            Effect e = effects.elementAt( i );
            if (!e.isComplete()) {
                e.onNext(data);
/*
                if( e.isComplete() ) {
                    e.reset();
                }
*/
                break;
            }
        }
/*
        if( isComplete() ) {
            reset();
            setActive( false );
        }
        */
    }

    public int getTextureID() {
        if( currentEffect == null )
            return -1;

        return currentEffect.getTextureID();
    }
}
