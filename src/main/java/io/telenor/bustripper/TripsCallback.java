package io.telenor.bustripper;

import java.io.IOException;
import java.util.Set;

/**
 * Callback class to client app requesting some bustrips.
 */
public interface TripsCallback {

    /**
     * Got a list of trips
     * @param trips the set of bus trips found
     */
    void gotTrips(Set<BusTrip> trips);

    /**
     * Faild getting the list of trips.
     m io trouble found
     */
    void failedGettingTrips(Exception io);

}
