package io.telenor.bustripper;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FINDS a busline for stop ID
 * http://reisapi.ruter.no/StopVisit/GetDepartures/2190021?datetime=2016-11-22T12:10:00
 */
public class FindBusLinesForStop {

    private static final String SEARCH_URL = "http://reisapi.ruter.no/StopVisit/GetDepartures/%s?datetime=%s";
    private static SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");


    public static void run(String stopId, TripsCallback callback) {

        String formattedDate = formatter.format(new Date());

        ClientConfig configuration = new ClientConfig();

        Client client = ClientBuilder.newClient(configuration);

        String target = String.format(SEARCH_URL, stopId, formattedDate);
        Invocation.Builder invocationBuilder = client
                .target(target)
                .request(MediaType.APPLICATION_JSON);

        final AsyncInvoker asyncInvoker = invocationBuilder.async();
        asyncInvoker.get(new BusTripsCallBack(target, callback));
    }

}
