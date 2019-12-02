package ca.duffyco.RenderEngine;

import java.util.Vector;

/**
 * Created by lidia on 3/13/2016.
 */
public class SequentialEventGroup<T> extends Effect<T>
{
    Vector<Effect> effects = new Vector<Effect>();
    private Texture texture;

    public SequentialEventGroup() {
    }

    public boolean isComplete() {
        for( Effect e : effects ) {
            if( !e.isComplete() ) {
                e.setActive(true);
                return false;
            }
        }
        return true;
    }

    public int getPhase() {
        int i=0;
        while( effects.get(i).isComplete() )
            i++;

        return i;
    }

    public boolean subDraw(float fpsRatio)
    {
        for( Effect e : effects ) {
            if (!e.isComplete()) {
                e.draw( fpsRatio );
            }
        }

        return true;
    }

    public void reset() {
        for( Effect e : effects ) {
            e.reset();
            e.setActive( false );
        }
    }


    public void subscribe( Effect<T> newEffect )
    {
        effects.add(newEffect);
    }

    public void unsubscribe( Effect<T> effect )
    {
        effects.remove(effects.indexOf(effect));
    }

    public void addTexture(Texture t)
    {
        this.texture = t;
        for( Effect<T> e : effects )
            e.addTexture( t );
    }

    public void onNext( T data )
    {
        if( !isActive && data.equals( Effect.OFF ) )
            return;
        else if( !isActive )
            setActive( true );

        /*
         *@TODO: Adjusted to turn on the next layer if complete
         */

        for( int i=0; i < effects.size(); i++ )
        {
            Effect e = effects.elementAt( i );
            if (!e.isComplete()) {
                e.onNext(data);

                if( e.isComplete() ) {
                    if ((i + 1) < effects.size() && !effects.elementAt(i + 1).isActive) {
                        effects.elementAt(i + 1).setActive(true);
                    }
                }

                break;
            }
        }

        if( isComplete() ) {
            reset();
            setActive( false );
        }
    }

    public int getTextureID() {
        return texture.textureID;
    }

}
