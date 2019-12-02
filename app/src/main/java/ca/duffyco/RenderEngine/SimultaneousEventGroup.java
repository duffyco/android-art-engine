package ca.duffyco.RenderEngine;

import java.util.Vector;

/**
 * Created by jon on 10/14/2016.
 */

public class SimultaneousEventGroup<T> extends Effect<T> {

    Vector<Effect> effects = new Vector<Effect>();
    private Texture texture;

    public SimultaneousEventGroup() {
    }

    public boolean isComplete() {
        for( Effect e : effects ) {
            if( !e.isComplete() ) {
                return false;
            }
        }
        return true;
    }

    public boolean subDraw(float fpsRatio)
    {
        for( Effect e : effects ) {
                e.draw( fpsRatio );
        }

        return true;
    }

    @Override
    public void setActive(boolean isActive) {
        super.setActive( isActive );

        for( Effect e : effects ) {
            e.setActive( isActive );
        }
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
                    e.reset();
                }
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

