package ca.duffyco.Piece;

import android.util.Log;

import java.util.Iterator;
import java.util.Random;

import ca.duffyco.RenderEngine.CoordinatedEventGroup;
import ca.duffyco.RenderEngine.Effect;
import ca.duffyco.RenderEngine.FadeInEffect;
import ca.duffyco.RenderEngine.FadeOutEffect;
import ca.duffyco.RenderEngine.MainActivity;
import ca.duffyco.RenderEngine.OnEffect;
import ca.duffyco.RenderEngine.Piece;
import ca.duffyco.RenderEngine.Renderer;
import ca.duffyco.RenderEngine.RotateEffect;
import ca.duffyco.RenderEngine.SequentialCoordinatedEventGroup;
import ca.duffyco.RenderEngine.SequentialEventGroup;
import ca.duffyco.RenderEngine.SimultaneousEventGroup;
import ca.duffyco.RenderEngine.Texture;
import ca.duffyco.events.CoordinatedEffectStore;
import ca.duffyco.events.Event;
import ca.duffyco.events.EventManager;
import ca.duffyco.sensor.MQTTSensor;
import ca.duffyco.sensor.Sensor;
import ca.duffyco.sensor.rxMqtt.exceptions.RxMqttException;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jon on 10/26/2016.
 */

public class swwitb extends Piece {

    private static Random generator = new Random();
    private int mProgramObject = -1;
    private MainActivity mContext = null;

    public swwitb( int programObject, MainActivity context )
    {
        mProgramObject = programObject;
        mContext = context;
    }

    public void setup()
    {
        String LIGHT_CHANNEL = "light";
        String ipAddress = "99.255.147.179";
        int port = 2758;

        initLightSensor(LIGHT_CHANNEL, ipAddress, port);

        EventManager.getInstance().subscribe(LIGHT_CHANNEL,
                new CoordinatedEffectStore() {
                    private int currentLayer = 0;
                    private int mode = 0;
                    private boolean firstChanged = true;
                    @Override
                    public void dispatch(Event<?> in) {

                        Event<Integer> e = (Event<Integer>) in;

                        if( e.eventMessage.intValue() != Effect.OFF )
                        {
                            if( !observers.elementAt( 0 ).isComplete() )
                            {
                                observers.elementAt( currentLayer ).onNext( in.eventMessage );
                            }
                            else {
                                observers.elementAt(0).setActive(false);
                                observers.elementAt( 0 ).reset();

                                for( int i=1; i < 6; i++ ) {
                                    observers.elementAt( i ).onNext(in.eventMessage);
                                }
                            }
                        }
                        else
                        {
                            for( int i=0; i < 6; i++ ) {
                                observers.elementAt(i).setActive(false);
                                observers.elementAt(i).reset();
                            }

                            observers.elementAt(6).onNext(in.eventMessage);

                            if( observers.elementAt(6).isComplete() )
                            {
                                observers.elementAt(6).setActive(false);
                                observers.elementAt(6).reset();
                            }
                        }
                    }
                });

        SequentialCoordinatedEventGroup<Integer> fadeInEventGroup = new SequentialCoordinatedEventGroup<Integer>();
        SequentialCoordinatedEventGroup<Integer> fadeOutEventGroup = new SequentialCoordinatedEventGroup<Integer>() {
            @Override
            protected boolean subDraw(float fpsRatio) {
                if( effectIter == null ) {
                    effectIter = (Iterator<Effect>) effects.iterator();
                    currentEffect = effectIter.next();
                }

                if( !currentEffect.isComplete() ) {
                    currentEffect.draw(fpsRatio);
                }
                else if( effectIter.hasNext() )
                {
                    currentEffect = effectIter.next();
                    currentEffect.onNext( 1 );
                }
                else
                    return false;

                return true;
            }
        };

        textures.add(new Texture(mContext, "textures/layer6.png"));
        textures.add(new Texture(mContext, "textures/layer5.png"));
        textures.add(new Texture(mContext, "textures/layer4.png"));
        textures.add(new Texture(mContext, "textures/layer3.png"));
        textures.add(new Texture(mContext, "textures/layer2.png"));
        textures.add(new Texture(mContext, "textures/layer1.png"));

        Texture screenTexture = new Texture(mContext, "textures/screen.png");


        Effect toAdd;

        for( int i=0; i < textures.size(); i++ )
        {
            FadeInEffect<Integer> fadeInEffect = new FadeInEffect<>(mProgramObject, 25.0f);
            fadeInEffect.addTexture(textures.get(i));
            fadeInEventGroup.subscribe( fadeInEffect );
        }

        toAdd = fadeInEventGroup;
        effects.add(toAdd);
        EventManager.getInstance().getEventStore("light").addObserver(toAdd);

        float maxX = 0.8f;
        float minX = 0.2f;

        for( int i=1; i < textures.size(); i++ )
        {
            SequentialEventGroup<Integer> sequentialEventGroup = new SequentialEventGroup<>();
            sequentialEventGroup.subscribe(new FadeOutEffect<Integer>(mProgramObject, generator.nextFloat() + 0.5f) );
            sequentialEventGroup.subscribe(new FadeInEffect<Integer>(mProgramObject, generator.nextFloat() + 0.5f) ) ;
            sequentialEventGroup.setRepeat( true );
            sequentialEventGroup.addTexture(textures.get(i));

            SimultaneousEventGroup<Integer> simultaneousEventGroup = new SimultaneousEventGroup<>();
            simultaneousEventGroup.subscribe(sequentialEventGroup);
            RotateEffect<Integer> rotateEffect = new RotateEffect<Integer>(mProgramObject, generator.nextFloat() * (maxX - minX) + minX);
            rotateEffect.setRepeat( true );
            simultaneousEventGroup.subscribe(rotateEffect);
            simultaneousEventGroup.addTexture(textures.get(i));

            toAdd = simultaneousEventGroup;

            effects.add(toAdd);
            EventManager.getInstance().getEventStore("light").addObserver(toAdd);
        }

        for( int i=textures.size() - 1; i > -1 ; i-- )
        {
            FadeOutEffect<Integer> fadeOutEffect = new FadeOutEffect<>(mProgramObject, 450.0f);
            fadeOutEffect.addTexture(textures.get(i));
            fadeOutEventGroup.subscribe( fadeOutEffect );
        }

        toAdd = fadeOutEventGroup;
        effects.add(toAdd);
        EventManager.getInstance().getEventStore("light").addObserver(toAdd);

        OnEffect<Integer> onEffect = new OnEffect<>( mProgramObject );
        onEffect.setActive( true );
        onEffect.addTexture( screenTexture );
        effects.add(onEffect);
        textures.add( screenTexture );
    }

    public void initLightSensor( String sensorName, String ipAddress, int port )
    {
        try {
            final Sensor<String> light = new MQTTSensor(0, sensorName, ipAddress, port);
            light.connect("light");
            light.subscribeTo()
                    .subscribeOn( Schedulers.io() )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {

                        @Override
                        public void onCompleted() {
                            Renderer.onError( "Connected To Server" );
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(Thread.currentThread().getName(), "subscribed failed");
                        }

                        @Override
                        public void onNext(String str) {
                            try {
                                EventManager.dispatch(
                                        new Event<Integer>(
                                                light.id,
                                                light.name,
                                                Event.EventType.READ,
                                                Integer.valueOf(str).intValue()
                                        ));
                            }
                            catch( Exception e )
                            {
                                Log.d( Thread.currentThread().getName(), e.getMessage() );
                            }
                        }
                    });;

        }
        catch( RxMqttException e )
        {
            e.printStackTrace();
        }
    }
}
