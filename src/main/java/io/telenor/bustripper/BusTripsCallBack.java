package io.telenor.bustripper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Callback from Jersey when bustrips are there.
 */
public class BusTripsCallBack implements InvocationCallback<Response> {
    ObjectMapper mapper = new ObjectMapper();
    String url;
    private TripsCallback listener;
    private boolean last;

    public BusTripsCallBack(String url, TripsCallback callback, boolean last) {
        this.url = url;
        this.listener = callback;
        this.last = last;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void completed(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        String content = response.readEntity(String.class);

        try {
            BusTrip[] trips = mapper.readValue(content, BusTrip[].class);
            HashSet set = new HashSet(Arrays.asList(trips));
            if(!set.isEmpty())
                listener.gotTrips(set, last);

        } catch (IOException e) {
            if(last) {
                listener.failedGettingTrips(e);
            }
        }

    }

    public void failed(Throwable throwable) {
        listener.failedGettingTrips((IOException) throwable);
    }
}
