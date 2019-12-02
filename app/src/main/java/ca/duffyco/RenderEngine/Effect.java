package ca.duffyco.RenderEngine;

/**
 * Created by lidia on 8/6/2015.
 */
public abstract class Effect<T> {

    public static Integer OFF = new Integer( -1 );

    protected int programObject;
    protected boolean isActive = false;
    protected boolean repeat = false;

    public boolean draw(float fpsRatio)
    {
        if( !isActive )
            return false;

        boolean comp = isComplete();
        if( !isComplete() ) {
            return subDraw( fpsRatio );
        }
        else if( repeat ) {
            reset();
            setActive( true );
        }


        return subDraw( fpsRatio ); // Cause it to stay in the last state.
    }

    public void setRepeat(boolean doRepeat)
    { this.repeat = doRepeat; }

    public void setActive(boolean isActive)
    { this.isActive = isActive; }

    public int getTextureID(int i)
    { return getTextureID(); }

    public abstract int getTextureID();
    protected abstract boolean subDraw(float fpsRatio);
    public abstract boolean isComplete();
    public abstract void onNext( T data );
    public abstract void addTexture(Texture t);
    public abstract void reset();
}
