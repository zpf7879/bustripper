package io.telenor.bustripper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.glassfish.jersey.client.ClientConfig;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

/**
 * Searches for bus stops in area provided.
 */
public class FindBusStop implements Runnable {

    private String searchTerm;
    private Client client;
    private TripsCallback listener;

    public FindBusStop(TripsCallback callback, String searchTerm) {
        this.listener = callback;
        this.searchTerm = searchTerm;
    }

    public void run() {
        ClientConfig configuration = new ClientConfig();

        client = ClientBuilder.newClient(configuration);

        Invocation.Builder invocationBuilder = null;

        try {
            URI uri = new URI(
                    "http",
                    "reisapi.ruter.no",
                    "/Place/GetPlaces/" + searchTerm,
                    null);
            String s = uri.toURL().toString();
            invocationBuilder = client
                        .target(s)
                        .request(MediaType.APPLICATION_JSON);
        } catch (URISyntaxException | MalformedURLException e) {
            listener.failedGettingTrips(e);
        }

        final AsyncInvoker asyncInvoker = invocationBuilder.async();
        BusStopsCallBack callback = new BusStopsCallBack(listener);
        asyncInvoker.get(callback);
    }
}
