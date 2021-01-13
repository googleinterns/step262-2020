package com.google.sps.data;

import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

public class DurationsFetcher {

    // The maximum durations in seconds, so that any duration higher than that
    // will not contribute to the place's score.
    private static final double MAX_DURATION_SECONDS = 40 * 60;

    // The entry point for a Google GEO API request.
    private GeoApiContext context;

    /**
     * PlacesScorerImpl constructor.
     *
     * @param geoApiContext the GeoApiContext used for all Google GEO API requests
     */
    public DurationsFetcher(GeoApiContext geoApiContext) {
        this.context = geoApiContext;
    }

    /**
     * Gets the driving duration from each place to the destination using Google Distance Matrix API,
     * relative to Max Duration.
     *
     * @param places a list of places do get durations from
     * @param destination the destination to calculate durations to
     * @return the relative duration from each place on places list to the destination
     * @throws IOException Thrown when an I/O exception of some sort has occurred
     * @throws InterruptedException Thrown when a thread is occupied and interrupted
     * @throws ApiException Thrown if the API returned result is an error
     */
    public ImmutableMap<Place, Double> getDurations(ImmutableList<Place> places, LatLng destination)
            throws ApiException, InterruptedException, IOException {
        ImmutableMap.Builder<Place, Double> durations = new ImmutableMap.Builder<>();
        LatLng[] origins = places.stream()
            .map(place -> place.location()).toArray(LatLng[]::new);
        DistanceMatrixApiRequest distanceRequest =
            DistanceMatrixApi.newRequest(context)
                .origins(origins)
                .destinations(destination)
                .mode(TravelMode.DRIVING);
        DistanceMatrix distanceMatrix = getDistanceResults(distanceRequest);
        for (int i = 0; i < places.size(); i++) {
            DistanceMatrixElement element = distanceMatrix.rows[i].elements[0];
            if (element.status == DistanceMatrixElementStatus.OK) {
                durations.put(places.get(i), element.duration.inSeconds / MAX_DURATION_SECONDS);
            } else { // TODO(Tal): decide if this place should be filtered out
                durations.put(places.get(i), 1D);
            }
        }
        return durations.build();
    }

    /**
    * Queries Google Distance Matrix API according to given query.
    *
    * @param distanceMatRequest A DistanceMatrixApiRequest with all places as origins
    *     and the user's location as the destination
    * @return A DistanceMatrix containig the distance and duration from each origin
    *     to the destination, each row in the matrix corresponds to an origin
    * @throws IOException Thrown when an I/O exception of some sort has occurred
    * @throws InterruptedException Thrown when a thread is occupied and interrupted
    * @throws ApiException Thrown if the API returned result is an error
    */
    @VisibleForTesting
    DistanceMatrix getDistanceResults(DistanceMatrixApiRequest distanceMatRequest)
            throws ApiException, InterruptedException, IOException {
        return distanceMatRequest.await();
    }
}
