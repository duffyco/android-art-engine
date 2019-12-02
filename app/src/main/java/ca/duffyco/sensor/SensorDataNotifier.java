package ca.duffyco.sensor;

/**
 * Created by lidia on 9/10/2015.
 */
public interface SensorDataNotifier<T>
{
    public void onError( Throwable e );
    public void onNext( T data );
}
