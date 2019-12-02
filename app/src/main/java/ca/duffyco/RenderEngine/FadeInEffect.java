package ca.duffyco.RenderEngine;


import android.opengl.GLES20;
import android.util.Pair;

/**
 * Created by lidia on 8/6/2015.
 */
public class FadeInEffect<T> extends Effect<T>
{
    private float alpha = 0.0f;
    private float alphaFactor = 1.0f;
    private Texture mTexture;
    private String key = "f_alpha";

    private static final float ALPHA_FACTOR = 0.003f;

    public FadeInEffect( int programObject ) {
        super.programObject = programObject;
        reset();
    }

    public FadeInEffect( int programObject, float inAlphaFactor ) {
        super.programObject = programObject;
        alphaFactor = inAlphaFactor;
        reset();
    }

    public void reset()
    {
        alpha = 0.0f;
    }

    public boolean isComplete() {
        return alpha >= 1.0f;
    }

    public boolean subDraw(float fpsRatio) {
        float nextAlpha = getAlpha( fpsRatio );
//        Log.d("alpha", String.valueOf( nextAlpha ) );
        mTexture.updateState( key, nextAlpha, this.getClass().getName() );
        return true;
    }

    public float getAlpha( float fpsRatio )
    {
        alpha += ALPHA_FACTOR * alphaFactor * fpsRatio;

        if( alpha > 1.0f ) {
            alpha = 1.0f;
        }

        return alpha;
    }

    public int getTextureID() { return mTexture.textureID; }

    public void addTexture(Texture t)
    {
        mTexture = t;
        mTexture.addState( key, Pair.create( GLES20.glGetUniformLocation(programObject, key), 0.0f));
    }

    public void onNext( T data )
    {
        if( !isActive )
            setActive( true );

        alpha = mTexture.getState( key );
    }
}
