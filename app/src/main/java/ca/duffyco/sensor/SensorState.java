package ca.duffyco.sensor;

/**
 * Created by lidia on 2/21/2016.
 */
public abstract class SensorState<T> implements SensorDataNotifier<T> {

    public abstract boolean activate(T data);
    public void onError( Throwable e )
    { e.printStackTrace(); }

    public void onNext( T data )
    { activate( data ); }
}
