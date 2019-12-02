package ca.duffyco.RenderEngine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends ActionBarActivity {
    private final int CONTEXT_CLIENT_VERSION = 2;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Full screen is set for the Window
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);

        Log.e("ca.duffyco", "Starting 2.0 ...");

        mGLSurfaceView = new GLSurfaceView( this );

        //if ( detectOpenGLES20() )
        //{
        // Tell the surface view we want to create an OpenGL ES 3.0-compatible
        // context, and set an OpenGL ES 3.0-compatible renderer.
        mGLSurfaceView.setEGLContextClientVersion(CONTEXT_CLIENT_VERSION);
        mGLSurfaceView.setRenderer(new Renderer( this ));
       /* }
        else
        {
            Log.e("MultiTexture", "OpenGL ES 3.0 not supported on device.  Exiting...");
            finish();

        }*/
        Log.e("ca.duffyco", "Set Renderer Done" );
        setContentView(mGLSurfaceView);
        Log.e("ca.duffyco", "onCreate Finished");
    }

    private boolean detectOpenGLES20()
    {
        ActivityManager am =
                ( ActivityManager ) getSystemService ( Context.ACTIVITY_SERVICE );
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return ( info.reqGlEsVersion >= 0x20000 );
    }

    @Override
    protected void onResume()
    {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private GLSurfaceView mGLSurfaceView;
}

