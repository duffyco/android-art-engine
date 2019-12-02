package ca.duffyco.RenderEngine;

import android.opengl.GLES20;
import android.util.Pair;

/**
 * Created by lidia on 8/6/2015.
 */
public class FadeOutEffect<T> extends Effect<T> {

    private int fAlpha;
    private float alpha = 1.0f;
    private float alphaFactor = 1.0f;
    private static final float ALPHA_FACTOR = 0.003f;
    private String key = "f_alpha";
    private Texture mTexture;

    public Texture texture;

    public FadeOutEffect( int programObject ) {
        super.programObject = programObject;
        reset();
    }

    public FadeOutEffect( int programObject, float inAlphaFactor ) {
        super.programObject = programObject;
        alphaFactor = inAlphaFactor;
        reset();
    }

    public void reset()
    {
        alpha = 1.0f;
    }

    public boolean isComplete() {
        if( alpha <= 0.0f )
            alpha = 0.0f;

        return alpha <= 0.0f;
    }

    public boolean subDraw(float fpsRatio) {
        float nextAlpha = getAlpha( fpsRatio );
        //Log.d("alpha", String.valueOf( nextAlpha ) );
        mTexture.updateState( key, nextAlpha, this.getClass().getName()  );
        return true;
    }

    public float getAlpha( float fpsRatio )
    {
        alpha -= ALPHA_FACTOR * alphaFactor * fpsRatio;

        if( alpha < 0.0f ) {
            alpha = 0.0f;
        }

        return alpha;
    }

    public int getTextureID() { return mTexture.textureID; }

    public void addTexture(Texture t)
    {
        mTexture = t;
        mTexture.addState( key, Pair.create( GLES20.glGetUniformLocation(programObject, key), 1.0f));

    }

    public void onNext( T data )
    {
        if( !isActive )
            setActive( true );

        alpha = mTexture.getState( key );
    }
}

