package io.telenor.bustripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

        /**
         * Waits for Bustrip results and print the 10 first results sorted by time.
        **/
        private static class BustripWaiter implements TripsCallback {
            private Set<BusTrip> allTrips = new HashSet<BusTrip>();
            private static int MAX_TRIPS = 10;
            private final AtomicBoolean timeout = new AtomicBoolean(false);
            private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);

            public BustripWaiter() {
                executor.schedule(() -> timeout.set(true), 10, TimeUnit.SECONDS);
            }

            @Override
            public synchronized void gotTrips(Set<BusTrip> trips) {
                allTrips.addAll(trips);
                notify();
            }

            @Override
            public synchronized void failedGettingTrips(Exception io) {
                System.out.println("Failed getting trips. " + io.getMessage());
                notify();
            }

            public synchronized void complete() {
                if (allTrips.isEmpty()) {
                    System.out.println("No trips found!");
                }

                allTrips.stream().sorted(
                        (e1, e2) -> e1.getExpectedArrivalTime().compareTo(e2.getExpectedArrivalTime())
                ).limit(MAX_TRIPS).forEach(t -> System.out.println(t));

                notify();
            }


            public synchronized void waitForCompletion() throws InterruptedException{
                while (!timeout.get()) {
                    wait(20);
                }
                complete();
            }
        }

        private static class InputGatherer implements Runnable {
            public void run() {
                System.out.println("Welcome to the bus line lookup tool. Add a search phrase to look up a bus stop.");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                boolean done = false;

                do {
                    System.out.print("> ");
                    try {
                        String searchterm = in.readLine();
                        if(searchterm.equals("q") || searchterm.length() == 0) {
                            System.exit(0);
                        }
                        System.out.println("Looking up " + searchterm);
                        BustripWaiter waiter = new BustripWaiter();
                        new Thread(new FindBusStop(waiter, searchterm)).start();
                        waiter.waitForCompletion();
                    }
                    catch(IOException | InterruptedException io) {
                        done = true;
                    }
                } while (!done);

            }
        }

        public static void main(String[] args) {
            new Thread(new InputGatherer()).start();
        }
    }
