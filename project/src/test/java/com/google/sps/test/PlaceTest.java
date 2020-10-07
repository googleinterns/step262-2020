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

package com.google.sps.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertThrows;

import com.google.maps.model.LatLng;
import com.google.sps.data.Place;


@RunWith(JUnit4.class)
public final class PlaceTest {

  /** A stub Place name. */
  private static final String VALID_NAME = "name";
  /** A stub Place website URL. */
  private static final String VALID_WEBSITE = "website@google.com";
  /** A stub Place phone number. */
  private static final String VALID_PHONE = "+97250-0000-000";
  /** A valid Place rating. */
  private static final int VALID_RATING = 4;
  /** A valid Place price level. */
  private static final int VALID_PRICE_LEVEL = 3;
  /** A stub Place location. */
  private static final LatLng VALID_LOCATION = new LatLng(35.35, 30.30);

  @Test
  public void create_invalidLowRating_throwsIllegalArgumentException() {
    int invalidLowRating = 0;

    assertThrows(IllegalArgumentException.class, () -> Place.create(VALID_NAME, VALID_WEBSITE,
        VALID_PHONE, invalidLowRating, VALID_PRICE_LEVEL, VALID_LOCATION));
  }

  @Test
  public void create_invalidHighRating_throwsIllegalArgumentException() {
    int invalidHighRating = 10;

    assertThrows(IllegalArgumentException.class, () -> Place.create(VALID_NAME, VALID_WEBSITE,
        VALID_PHONE, invalidHighRating, VALID_PRICE_LEVEL, VALID_LOCATION));
  }

  @Test
  public void create_invalidLowPriceLevel_throwsIllegalArgumentException() {
    int invalidLowPriceLevel = -1;

    assertThrows(IllegalArgumentException.class, () -> Place.create(VALID_NAME, VALID_WEBSITE,
        VALID_PHONE, VALID_RATING, invalidLowPriceLevel, VALID_LOCATION));
  }

  @Test
  public void create_invalidHighPriceLevel_throwsIllegalArgumentException() {
    int invalidHighPriceLevel = 5;

    assertThrows(IllegalArgumentException.class, () -> Place.create(VALID_NAME, VALID_WEBSITE,
        VALID_PHONE, VALID_RATING, invalidHighPriceLevel, VALID_LOCATION));
  }
}
