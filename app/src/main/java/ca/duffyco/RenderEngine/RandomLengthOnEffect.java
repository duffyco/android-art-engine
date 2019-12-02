package ca.duffyco.RenderEngine;

import android.util.Log;

import java.util.Random;

/**
 * Created by lidia on 8/6/2015.
 */
public class RandomLengthOnEffect<T> extends Effect<T>{

    private int timeOnMS = 0;
    long startTime = 0;
    private boolean isComplete = false;

    public RandomLengthOnEffect(int programObject)
    {
        super.programObject = programObject;
        reset();
    }

    public static Random generator = new Random();


    public void reset()
    {
        isComplete = false;
        startTime = 0;
    }

    public void genDuration()
    {
        timeOnMS = generator.nextInt( 5000 ) + 1000;
        Log.e("ca.duffyco", "genDuration: " + timeOnMS );
    }

    public boolean isComplete() {

        return isComplete;
    }

    public boolean subDraw(float fpsRatio)
    {
        if( startTime == 0 ) {
            startTime = System.nanoTime();
        }
        else if ( ( ( System.nanoTime() - startTime ) / 1000000L ) > timeOnMS )
        {
            isComplete = true;
        }

        return true;
    }

    public int getTextureID() { return 0; }

    public void addTexture(Texture t)
    {
    }

    public void onNext( T data )
    {
        if( !isActive )
            setActive( true );
    }
}
