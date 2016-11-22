package io.telenor.bustripper;

import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class BusStopsCallBack implements InvocationCallback<Response> {

    private ObjectMapper mapper = new ObjectMapper();

    private TripsCallback listener;

    public BusStopsCallBack(TripsCallback callback) {
        this.listener = callback;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void completed(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BusStop[] stops = mapper.readValue(response.readEntity(String.class), BusStop[].class);
            System.out.println(String.format("Got %d busstops nearby", stops.length));

            for(int i = 0; i< stops.length && i<10;i++) {
                BusStop stop = stops[i];
                boolean isLast = stop == stops[stops.length -1];
                new Thread(new FindBusLinesForStop(stop.getId(), listener, isLast)).start();
            }
        } catch (IOException e) {
            listener.failedGettingTrips(e);
        }

    }

    public void failed(Throwable throwable) {
        listener.failedGettingTrips((IOException) throwable);
    }
}
