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

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.common.collect.ImmutableList;
import com.google.maps.model.LatLng;

@RunWith(JUnit4.class)
public class UserPreferencesTest {

    private static final float RATING = 4;
    private static final int PRICE_LEVEL = 2;
    private static final LatLng LOCATION = new LatLng(32.08074, 34.78059);
    private static final ImmutableList<String> CUISINES = ImmutableList.of("sushi", "burger");
    private static final boolean OPEN_NOW = true;

    @Test
    public void build_invalidLowRating_throwsIllegalArgumentException() {
      int invalidLowRating = 0;

      assertThrows(IllegalArgumentException.class, () -> {
        getValidUserPreferencesBuilder().setMinRating(invalidLowRating).build();
      });
    }

    @Test
    public void build_invalidHighRating_throwsIllegalArgumentException() {
      int invalidHighRating = 10;

      assertThrows(IllegalArgumentException.class, () -> {
        getValidUserPreferencesBuilder().setMinRating(invalidHighRating).build();
      });
    }

    @Test
    public void build_invalidLowPriceLevel_throwsIllegalArgumentException() {
      int invalidLowPriceLevel = -1;

      assertThrows(IllegalArgumentException.class, () -> {
        getValidUserPreferencesBuilder().setMaxPriceLevel(invalidLowPriceLevel).build();
      });
    }

    @Test
    public void build_invalidHighPriceLevel_throwsIllegalArgumentException() {
      int invalidHighPriceLevel = 5;

      assertThrows(IllegalArgumentException.class, () -> {
        getValidUserPreferencesBuilder().setMaxPriceLevel(invalidHighPriceLevel).build();
      });
    }

    @Test
    public void build_validInput_returnsValidUserPreferences() {
        UserPreferences preferences = getValidUserPreferencesBuilder().build();
        assertAll("userPreferences",
            () -> assertEquals(RATING, preferences.minRating()),
            () -> assertEquals(PRICE_LEVEL, preferences.maxPriceLevel()),
            () -> assertEquals(LOCATION, preferences.location()),
            () -> assertEquals(CUISINES, preferences.cuisines()),
            () -> assertEquals(OPEN_NOW, preferences.openNow())
        );
    }

    // Returns a UserPreferences builder that has valid values of all attributes.
    private UserPreferences.Builder getValidUserPreferencesBuilder() {
      return UserPreferences.builder()
          .setMinRating(RATING)
          .setMaxPriceLevel(PRICE_LEVEL)
          .setLocation(LOCATION)
          .setCuisines(CUISINES)
          .setOpenNow(OPEN_NOW);
    }
}
