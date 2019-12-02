package ca.duffyco.sensor;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttToken;

import ca.duffyco.RenderEngine.Renderer;
import ca.duffyco.sensor.rxMqtt.enums.ClientType;
import ca.duffyco.sensor.rxMqtt.exceptions.RxMqttException;
import ca.duffyco.sensor.rxMqtt.impl.RxMqttAsyncClient;
import ca.duffyco.sensor.rxMqtt.impl.RxMqttClientFactory;
import ca.duffyco.sensor.rxMqtt.impl.RxMqttClientStatus;
import ca.duffyco.sensor.rxMqtt.impl.RxMqttMessage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by lidia on 3/13/2016.
 */
public class MQTTSensor<T> extends Sensor
{
    private RxMqttAsyncClient client;
    private String topic;

    public MQTTSensor( int id, String name, String address, int port ) throws RxMqttException
    {
        super.id = id;
        super.name = name;

        client =  (RxMqttAsyncClient) (new RxMqttClientFactory()).create(
            address, port, name, false, ClientType.Async, null);
    }



    public Subscription connect( final String topic) {
        this.topic = topic;

                return client.connect().subscribe(new Observer<IMqttToken>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(Thread.currentThread().getName(), "connected failed");
                Renderer.onError( "Server Connect Failed");
            }

            @Override
            public void onNext(IMqttToken iMqttToken) {
                Log.d(Thread.currentThread().getName(), "connected");
                client.subscribeTopic(topic, 2).subscribe(new Observer<IMqttToken>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(Thread.currentThread().getName(), "subscribed failed");
                        Renderer.onError( "Server Connection Down");
                    }

                    @Override
                    public void onNext(IMqttToken iMqttToken) {
                        MqttToken token = (MqttToken) iMqttToken;
                        Log.d(Thread.currentThread().getName(),
                                "subscribed" + token.getTopics()[0]);

                        client.publish("outTopic", "Starting Client ....".getBytes()).subscribe(new Action1<IMqttToken>() {
                            @Override
                            public void call(IMqttToken iMqttToken) {
                                Log.d(Thread.currentThread().getName(), "published");

                                Renderer.onError( "Server Connected");
                                client.statusReport().subscribe(new Action1<RxMqttClientStatus>() {
                                @Override
                                public void call(RxMqttClientStatus rxMqttClientStatus) {
                                    Renderer.onError( "Server Connection Dropped");
                                }
                            });

                            }
                        });

                    }
                });
            }
        });

    }

    public Observable<IMqttToken> disconnect() {
        return client.disconnect();
    }

    public Observable<String> subscribeTo() {
        return client.subscribing( this.topic )
                .map( new Func1<RxMqttMessage, String> () {
                    @Override
                    public String call( RxMqttMessage message )
                    {
                        return message.getMessage();
                    }
                    });
                }

/*

        client.statusReport().subscribe(new Action1<RxMqttClientStatus>() {
            @Override
            public void call(RxMqttClientStatus rxMqttClientStatus) {
                Log.d(Thread.currentThread().getName(), rxMqttClientStatus.toString());
            }
        });

    } catch (RxMqttException ex) {

    }
    }
    */
}
