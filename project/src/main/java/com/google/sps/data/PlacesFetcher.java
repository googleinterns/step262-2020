// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PriceLevel;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;

import java.io.IOException;

import com.google.maps.*;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;


public final class PlacesFetcher {

    /**
     * Temporary fields for initial version. In next versions those fields will be
     * the fields of a UserPrefrences instance passed to the fetcher by the Servlet.
     */
    private static final LatLng location = new LatLng(32.080576, 34.780641); // Rabin Square TLV
    private static final String cuisineType = "sushi"; // TODO: change to set of types
    private static final PriceLevel maxPriceLevel = PriceLevel.VERY_EXPENSIVE; // TODO: map int to PrivceLevel
    private static final boolean openNow = true;

    /**
     * The type of places that will be searched
     */
    private static final PlaceType TYPE = PlaceType.RESTAURANT;

    /**
     * The search radius for places
     */
    private static int SEARCH_RADIUS = 5000; //TODO (M1): check at least 10 results, and if less extend radius 

    /**
     * The entry point for a Google GEO API request
     */
    private static GeoApiContext context = new GeoApiContext.Builder()
        .apiKey("AIza...")
        .build();

    /**
     * Builds a query and requests it from Google Places API.
     * 
     * @return list of places that supply the query.
     * @throws IOException
     * @throws InterruptedException
     * @throws ApiException
     */
    public static PlacesSearchResult[] fetch() throws IOException, InterruptedException, ApiException {
        PlacesSearchResponse results = 
            PlacesApi.textSearchQuery(context, cuisineType, location)
                .radius(SEARCH_RADIUS)
                .maxPrice(maxPriceLevel)
                .openNow(openNow)
                .type(TYPE)
                .await();
        context.shutdown();
        return results.results;
   }

    /**
     * A private constructor so instances are disallowed.
    */
    public PlacesFetcher() { }

}