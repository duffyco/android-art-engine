package ca.duffyco.Piece;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ca.duffyco.RenderEngine.CoordinatedEventGroup;
import ca.duffyco.RenderEngine.Effect;
import ca.duffyco.RenderEngine.FadeInEffect;
import ca.duffyco.RenderEngine.FadeOutEffect;
import ca.duffyco.RenderEngine.MainActivity;
import ca.duffyco.RenderEngine.OnEffect;
import ca.duffyco.RenderEngine.Piece;
import ca.duffyco.RenderEngine.RandomLengthOnEffect;
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
 * @TODO
 * Crash on Sending -1 sometimes?  CurrentEffect is being nulled
 */

public class Sketches extends Piece {
    private static Random generator = new Random();
    private int mProgramObject = -1;
    private MainActivity mContext = null;

    public Sketches(int programObject, MainActivity context )
    {
        mProgramObject = programObject;
        mContext = context;
    }

    public void setup()
    {
        String LIGHT_CHANNEL = "light";
        String ipAddress = "192.168.1.58";
        int port = 1883;

        final ArrayList<Texture> introTextures = new ArrayList<Texture>();
        introTextures.add(new Texture(mContext, "textures/introlayer0.png"));
        introTextures.add(new Texture(mContext, "textures/introlayer1.png"));
        introTextures.add(new Texture(mContext, "textures/introlayer2.png"));
        introTextures.add(new Texture(mContext, "textures/introlayer3.png"));
        introTextures.add(new Texture(mContext, "textures/introlayer4.png"));

        final ArrayList<Texture> seqTextures = new ArrayList<Texture>();
        seqTextures.add(new Texture(mContext, "textures/Layer13.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer12.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer11.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer10.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer9.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer8.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer6.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer5.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer3.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer2.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer1.png"));
        seqTextures.add(new Texture(mContext, "textures/Layer0.png"));


        initLightSensor(LIGHT_CHANNEL, ipAddress, port);

        EventManager.getInstance().subscribe(LIGHT_CHANNEL,
                new CoordinatedEffectStore() {
                    private int currentLayer = Effect.OFF;
                    private int mode = 0;
                    private boolean firstChanged = true;
                    @Override
                    public void dispatch(Event<?> in) {

                        Event<Integer> e = (Event<Integer>) in;

                        if( e.eventMessage.intValue() != Effect.OFF )
                        {
                            if( !observers.elementAt( 0 ).isComplete() && currentLayer == Effect.OFF )
                            {
                                observers.elementAt( 0 ).onNext( in.eventMessage );
                            }
                            else {
                                if( currentLayer == Effect.OFF ) {
                                    observers.elementAt(0).setActive(false);
                                    observers.elementAt(0).reset();
                                }

                                observers.elementAt(1).setActive(true);
                                observers.elementAt(1).onNext(in.eventMessage);
                                observers.elementAt(2).setActive(true);
                                observers.elementAt(2).onNext(in.eventMessage);
                            }
                        }
                        else
                        {
                            Effect fadeOutObserver = observers.elementAt(3);

                            for( Effect<Integer> observer: observers )
                            {
                                if( observer.equals( fadeOutObserver ) )
                                {
                                    fadeOutObserver.setActive( true );
                                    fadeOutObserver.onNext(in.eventMessage);
                                    if( !observer.isComplete() )
                                        continue;
                                }

                                observer.setActive( false );
                                observer.reset();
                            }

                            currentLayer = Effect.OFF;
                        }
                    }
                });

        SequentialCoordinatedEventGroup<Integer> fadeInEventGroup = new SequentialCoordinatedEventGroup<>();

        CoordinatedEventGroup<Integer> randomEventGroup = new CoordinatedEventGroup<Integer>() {

            @Override
            protected boolean subDraw(float fpsRatio) {

                for( Effect e : effects )
                {
                    if( !e.isComplete() )
                        e.draw( fpsRatio );
                    else {
                        e.reset();
                        e.setActive( false );
                    }
                }

                if( currentEffect == null )
                    currentEffect = effects.get(generator.nextInt(seqTextures.size() - 1) + 1);

                if( ( ( SequentialEventGroup<Integer> ) currentEffect ).getPhase() >= 2 ) {
                    Effect newEffect = effects.get(generator.nextInt(seqTextures.size() - 1) + 1);

                    while (newEffect.equals(currentEffect))
                        newEffect = effects.get(generator.nextInt(seqTextures.size() - 1) + 1);

                    currentEffect = newEffect;
                }

                currentEffect.setActive( true );
                return true;
            }

            @Override
            public int getTextureID(int i) {
                if (i < currentEffect.getTextureID())
                    return i;

                return getTextureID();
            }
        };

        SequentialCoordinatedEventGroup<Integer> fadeOutEventGroup = new SequentialCoordinatedEventGroup<Integer>() {
            @Override
            protected boolean subDraw(float fpsRatio) {
                if( effectIter == null ) {
                    effectIter = (Iterator<Effect>) effects.iterator();
                    currentEffect = effectIter.next();
                }

                try {
                    if (!currentEffect.isComplete()) {
                        currentEffect.draw(fpsRatio);
                    } else if (effectIter.hasNext()) {
                        currentEffect = effectIter.next();
                        currentEffect.onNext(1);
                    } else
                        return false;
                }
                catch( NullPointerException ee )
                {
                    return false;
                }
                return true;
            }
        };

        Effect toAdd;

        for( int i=0; i < introTextures.size(); i++ )
        {
            FadeInEffect<Integer> fadeInEffect = new FadeInEffect<>(mProgramObject, 2.0f);
            fadeInEffect.addTexture(introTextures.get(i));
            fadeInEventGroup.subscribe( fadeInEffect );
        }

        toAdd = fadeInEventGroup;
        effects.add(toAdd);
        EventManager.getInstance().getEventStore(LIGHT_CHANNEL).addObserver(toAdd);

        textures.addAll( introTextures );

        SimultaneousEventGroup<Integer> fadeOutIntroEventGroup = new SimultaneousEventGroup<>();
        for( Texture t : introTextures )
        {
            FadeOutEffect<Integer> fadeOutEffect = new FadeOutEffect<>(mProgramObject);
            fadeOutEffect.addTexture(t);
            fadeOutIntroEventGroup.subscribe( fadeOutEffect );
        }

        toAdd = fadeOutIntroEventGroup;
        effects.add(toAdd);
        EventManager.getInstance().getEventStore("light").addObserver(toAdd);

        for( int i=0; i < seqTextures.size(); i++ )
        {
            SequentialEventGroup<Integer> sequentialEventGroup = new SequentialEventGroup<Integer>();
            sequentialEventGroup.subscribe(new FadeInEffect<Integer>(mProgramObject));
            sequentialEventGroup.subscribe(new RandomLengthOnEffect<Integer>(mProgramObject) ) ;
            sequentialEventGroup.subscribe(new FadeOutEffect<Integer>(mProgramObject));
            sequentialEventGroup.addTexture(seqTextures.get(i));
            randomEventGroup.setRepeat( true );
            randomEventGroup.subscribe( sequentialEventGroup );

        }

        toAdd = randomEventGroup;
        effects.add(toAdd);
        EventManager.getInstance().getEventStore(LIGHT_CHANNEL).addObserver(toAdd);
        textures.addAll( seqTextures );

//        SimultaneousEventGroup<Integer> fadeOutEventGroup = new SimultaneousEventGroup<>();
        for( Texture t: textures )
        {
            FadeOutEffect<Integer> fadeOutEffect = new FadeOutEffect<>(mProgramObject, 2.0f);
            fadeOutEffect.addTexture(t);
            fadeOutEventGroup.subscribe( fadeOutEffect );
        }

        toAdd = fadeOutEventGroup;
        effects.add(toAdd);
        EventManager.getInstance().getEventStore("light").addObserver(toAdd);
    }

    public void initLightSensor( String sensorName, String ipAddress, int port )
    {
        try {
            final Sensor<String> light = new MQTTSensor(0, sensorName, ipAddress, port);
            light.connect(sensorName);
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
