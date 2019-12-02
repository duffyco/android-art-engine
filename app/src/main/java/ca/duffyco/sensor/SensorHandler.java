package ca.duffyco.sensor;

/**
 * Created by lidia on 2/6/2016.
 */
public class SensorHandler
{
    private int sensorID;
    private String name = "";

    public SensorHandler( int sensorID, String name )
    {
        this.sensorID = sensorID;
        this.name = name;
    }

    void connectSensor( String address )
    {
    }

    void disconnect()
    {
    }
}
