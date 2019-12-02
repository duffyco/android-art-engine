package ca.duffyco.RenderEngine;

/**
 * Created by lidia on 8/4/2015.
 */

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import ca.duffyco.Piece.Sketches;
import ca.duffyco.common.ESShader;

public class Renderer implements GLSurfaceView.Renderer{

    private static MainActivity mContext;
    private int mProgramObject;
    private int mBaseMapLoc;

    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;
    private TextView fpsView;

    private int fAlpha;
    private int uAngle;

    private static float FPS = 30.0f;
    private long lastCall = System.nanoTime();

    public Renderer ( MainActivity context )
    {
        Log.e("duffyco.ca", "Renderer");
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mIndices = ByteBuffer.allocateDirect ( mIndicesData.length * 2 )
                .order ( ByteOrder.nativeOrder() ).asShortBuffer();
        mIndices.put ( mIndicesData ).position(0);
    }

    private ArrayList<Effect<?>> effects = null;
    private ArrayList<Texture> textures = null;
    private Piece piece = null;

    public static void onError( final String message )
    {
        mContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText( mContext, message, Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void onSurfaceCreated ( GL10 glUnused, EGLConfig config )
    {
        Log.e("ca.duffyco", "onSurfaceCreated");

        fpsView = (TextView) mContext.findViewById( R.id.fpsTextView );

        Log.e("duffyco.ca", "Adding Textures");
        if( piece == null )
        {
            // Load shaders from 'assets' and get a linked program object
            mProgramObject = ESShader.loadProgramFromAsset(mContext,
                    "shaders/vertexShader.vert",
                    "shaders/fragmentShader.frag");

            piece = new Sketches( mProgramObject, mContext );
            piece.setup();
            textures = piece.getTextures();
            effects = piece.getEffects();

            mBaseMapLoc = GLES20.glGetUniformLocation(mProgramObject, "s_baseMap");
            fAlpha = GLES20.glGetUniformLocation(mProgramObject, "f_alpha");
            uAngle = GLES20.glGetUniformLocation(mProgramObject, "uAngle");
        }


       GLES20.glClearColor ( 0.0f, 0.0f, 0.0f, 0.0f );
    }

    float sumAvgFPS = FPS;
    int frames = 1;
    int printInterval = (int) FPS;


    public void onDrawFrame ( GL10 glUnused )
    {
        // FPS Measurement - Averaged
        float duration = ( System.nanoTime() - lastCall ) / 1000000f;

        // Angle Test
        sumTime += duration;
        // End Angle Test
        sumAvgFPS += 1000f / duration;
        frames++;

        float avgFPS = sumAvgFPS / frames;

        if( frames > FPS*2 )
        {
            sumAvgFPS -= avgFPS;
            frames = (int)FPS*2;
        }

        printInterval--;
        if( printInterval < 0) {
            Log.d("ca.duffyco", "Call: " + (float) (System.nanoTime() - lastCall) / 1000000f + "ms - " + 1000f / duration + " FPS [" + avgFPS + "avg]");
        }
        lastCall = System.nanoTime();

        // Set the view-port
        GLES20.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Use the program object
        GLES20.glUseProgram(mProgramObject);

        // Load the vertex position
        mVertices.position ( 0 );
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT,
                false,
                5 * 4, mVertices);
        // Load the texture coordinate
        mVertices.position ( 3 );
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT,
                false,
                5 * 4,
                mVertices);

        GLES20.glEnableVertexAttribArray(0);
        GLES20.glEnableVertexAttribArray(1);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Update State
        for( Effect e : effects )
            e.draw( (float)( FPS / avgFPS ) );

        for( Texture t: textures )
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.textureID);

            Iterator<Pair<Integer,Float>> initIter = t.getInitState();
            while( initIter.hasNext() ) {
                Pair<Integer, Float> next = initIter.next();
                GLES20.glUniform1f( next.first, next.second);
            }

            Iterator<Pair<Integer,Float>> iter = t.getState();
            while( iter.hasNext() ) {
                Pair<Integer, Float> next = iter.next();
                GLES20.glUniform1f( next.first, next.second);
            }

            if( t.getState( "f_alpha" ) == 0.0f )
                continue;


            GLES20.glUniform1i(mBaseMapLoc, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndices);
        }

        /* @ ANGLE TEST For Varying FPS - Should Angle be the same?

                uAngleTest += textures.get( 5 ).getState( "uAngle");

                if( sumTime > 1000 )
                {
                   // Log.d( "ca.duffyco", "uAngle: " + (float)( uAngleTest - lastTest ) + " in " + sumTime + "s" );
                    lastTest = uAngleTest;
                    uAngleTest = 0;
                    sumTime = 0;
                }
        */
        if( printInterval < 0) {
            printInterval =  (int) FPS;
        }

    }

    // Angle Test
    float sumTime = 0;
    float uAngleTest = 0;
    float lastTest = 0;


    public void onSurfaceChanged ( GL10 glUnused, int width, int height )
    {
        Log.e("duffyco.ca", "onSurfaceChanged " + width + " - " + height );

        mWidth = width;
        mHeight = height;
    }

    private final float[] mVerticesData =
            {
                    -1.0f,  1.0f, 0.0f, // Position 0
                    0.0f,  0.0f,       // TexCoord 0
                    -1.0f, -1.0f, 0.0f, // Position 1
                    0.0f,  1.0f,       // TexCoord 1
                    1.0f, -1.0f, 0.0f, // Position 2
                    1.0f,  1.0f,       // TexCoord 2
                    1.0f,  1.0f, 0.0f, // Position 3
                    1.0f,  0.0f        // TexCoord 3
            };

    private final short[] mIndicesData =
            {
                    0, 1, 2, 0, 2, 3
            };
}
