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
import com.google.sps.data.Place;


@RunWith(JUnit4.class)
public final class PlaceTest {

  @Test
  public void create_invalidLowRating_throwsIllegalArgumentException() {
    int invalidLowRating = 0;

    assertThrows(IllegalArgumentException.class, () -> {
      getValidPlaceBuilder().setRating(invalidLowRating).build();
    });
  }

  @Test
  public void create_invalidHighRating_throwsIllegalArgumentException() {
    int invalidHighRating = 10;

    assertThrows(IllegalArgumentException.class, () -> {
      getValidPlaceBuilder().setRating(invalidHighRating).build();
    });
  }

  @Test
  public void create_invalidLowPriceLevel_throwsIllegalArgumentException() {
    int invalidLowPriceLevel = -1;

    assertThrows(IllegalArgumentException.class, () -> {
      getValidPlaceBuilder().setPriceLevel(invalidLowPriceLevel).build();
    });
  }

  @Test
  public void create_invalidHighPriceLevel_throwsIllegalArgumentException() {
    int invalidHighPriceLevel = 5;

    assertThrows(IllegalArgumentException.class, () -> {
      getValidPlaceBuilder().setPriceLevel(invalidHighPriceLevel).build();
    });
  }

  /**
   * @return a Place builder that has valid values of all attributes
   */
  private Place.Builder getValidPlaceBuilder() {
    return Place.builder()
        .setName("name")
        .setWebsiteUrl("website@google.com")
        .setPhone("+97250-0000-000")
        .setRating(4)
        .setPriceLevel(3)
        .setLongitude(35.35)
        .setLatitude(30.30);
  }
}