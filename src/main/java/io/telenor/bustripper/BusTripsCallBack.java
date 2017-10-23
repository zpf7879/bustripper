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

    public BusTripsCallBack(String url, TripsCallback callback) {
        this.url = url;
        this.listener = callback;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void completed(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        String content = response.readEntity(String.class);

        if (content.isEmpty()) {
            return;
        }

        try {
            BusTrip[] trips = mapper.readValue(content, BusTrip[].class);
            HashSet set = new HashSet(Arrays.asList(trips));
            if(!set.isEmpty())
                listener.gotTrips(set);

        } catch (IOException e) {
            listener.failedGettingTrips(e);
        }
    }

    public void failed(Throwable throwable) {
        listener.failedGettingTrips((IOException) throwable);
    }
}
