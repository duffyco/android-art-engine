package ca.duffyco.RenderEngine;

import android.opengl.GLES20;
import android.util.Pair;

/**
 * Created by jon on 10/14/2016.
 */

public class RotateEffect<T> extends Effect<T>
{
    private int uAngle;
    private float angle = 0.0f;
    private float angleFactor = 1.0f;
    private Texture mTexture;
    private String key = "uAngle";

    private static final float ANGLE_FACTOR = 0.005f;

    public RotateEffect( int programObject ) {
        super.programObject = programObject;
        reset();
    }

    public RotateEffect( int programObject, float inAngleFactor) {
        super.programObject = programObject;
        angleFactor = inAngleFactor;
        reset();
    }

    public void reset()
    {
        angle = 0.0f;
    }

    public boolean isComplete() {
        return angle >= 360.0f;
    }

    public boolean subDraw(float fpsRatio) {
        float nextAngle = getAngle( fpsRatio );
//        Log.d("uAngle", String.valueOf( nextAngle ) );
        mTexture.updateState( key, nextAngle, this.getClass().getName()  );
        return true;
    }

    public float getAngle( float fpsRatio )
    {
        angle += angleFactor * fpsRatio * ANGLE_FACTOR;

        if( angle > 360.0f ) {
            angle = 360.0f;
        }

        return angle;
    }

    public int getTextureID() { return 0; }

    public void addTexture(Texture t)
    {
        mTexture = t;
        mTexture.addState( key, Pair.create( GLES20.glGetUniformLocation(programObject, key), 0.0f));
    }

    public void onNext( T data )
    {
        if( !isActive )
            setActive( true );

        angle = mTexture.getState( key );
    }
}

