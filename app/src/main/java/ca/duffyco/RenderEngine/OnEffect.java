package ca.duffyco.RenderEngine;

import android.opengl.GLES20;
import android.util.Pair;

/**
 * Created by lidia on 8/6/2015.
 */
public class OnEffect<T> extends Effect<T>{

    private int fAlpha;
    private Texture mTexture;
    private boolean isComplete;
    private String key = "f_alpha";

    public OnEffect(int programObject)
    {
        super.programObject = programObject;
    }

    public void reset()
    {
        isComplete = false;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean subDraw(float fpsRatio)
    {
//        mTexture.updateState( key, 1.0f);
        return true;
    }

    public int getTextureID() { return mTexture.textureID; }

    public void addTexture(Texture t)
    {
        mTexture = t;
        mTexture.addState( key, Pair.create( GLES20.glGetUniformLocation(programObject, key), 1.0f));
    }

    public void onNext( T data )
    {
    }
}
