package ca.duffyco.RenderEngine;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Texture {
    LinkedHashMap<String, Pair<Integer, Float>> state = new LinkedHashMap<>();
    static LinkedHashMap<String, Pair<Integer, Float>> initState = new LinkedHashMap<>();
//    Stack<Record> updates = new Stack<>();

    class Record {
        private String key;
        private String value;
        private String updater;

        public Record(String key, String value, String updater) {
            this.key = key;
            this.value = value;
            this.updater = updater;
        }
    }

    public Texture(Context ctx, String filename) {
        this.filename = filename;
        textureID = loadTextureFromAsset(ctx);
    }

    public void updateState(String key, Float value, String className) {
        state.put(key, Pair.create(state.get(key).first, value));
     //   updates.add(new Record(key, value.toString(), className));
       // updates.setSize( 20 );
    }

    static public Iterator<Pair<Integer, Float>> getInitState() {
        return initState.values().iterator();
    }

    public Iterator<Pair<Integer, Float>> getState() {
        return state.values().iterator();
    }

    public float getState(String key) {
        return state.get( key ).second.floatValue();
    }


    public void addState( String key, Pair<Integer,Float> value ) {
        if (!state.containsKey(key)) {
            state.put(key, value);
            initState.put(key, value);
        }
    }

    public void removeState( String key )
    {
        state.remove( key );
    }

    public void resetState()
    { state.clear(); }

    public int textureID = -1;
    public String filename;

    private int loadTextureFromAsset ( Context ctx )
    {
        int[] textureId = new int[1];
        Bitmap bitmap = null;
        InputStream is = null;

        try
        {
            is = ctx.getAssets().open(filename);
        }
        catch ( IOException ioe )
        {
            is = null;
        }

        if ( is == null )
        {
            Log.e("duffyco.ca", "Failed to load: " + filename);
            return 0;
        }

        bitmap = BitmapFactory.decodeStream(is);

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );

        return textureId[0];
    }
}
