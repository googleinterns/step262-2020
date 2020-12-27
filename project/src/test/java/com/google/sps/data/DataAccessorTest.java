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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DataAccessorTest {

  // A helper that enables us to test datastore locally.
  // Has to be set up and teared down for each test.
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  // A datastore service instance, that shall be created locally
  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  // The tested dataAccessor, which will be initialized with a datastore service instance.
  private DataAccessor dataAccessor;

  private static final String USER_ID = "12345";
  private static final String PLACE_ID_1 = "place1";
  private static final String PLACE_ID_2 = "place2";
  private static final String PLACE_ID_3 = "place3";

  @Before
  public void setUp() {
    helper.setUp();
    dataAccessor = new DataAccessor(datastoreService);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void isRegistered_registered_true() {
    Entity userEntity = new Entity(DataAccessor.USER_ENTITY_NAME, USER_ID);
    datastoreService.put(userEntity);

    assertTrue(dataAccessor.isRegistered(USER_ID));
  }

  @Test
  public void isRegistered_notRegistered_false() {
    String registeredUserId = "12345";
    String unRegisteredUserId = "54321";
    Entity userEntity = new Entity(DataAccessor.USER_ENTITY_NAME, registeredUserId);
    datastoreService.put(userEntity);

    assertFalse(dataAccessor.isRegistered(unRegisteredUserId));
  }

  @Test
  public void isRegistered_emptyUserId_throwIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> dataAccessor.isRegistered(""));
  }

  @Test
  public void isRegistered_nullUserId_throwIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> dataAccessor.isRegistered(null));
  }

  @Test
  public void registerUser_validUserId_registerUser() {
    dataAccessor.registerUser(USER_ID);
    List<Entity> results = createPreparedQueryByUserIdAsKey(USER_ID)
        .asList(FetchOptions.Builder.withDefaults());

    assertEquals(1, results.size());
    assertEquals(new Entity(DataAccessor.USER_ENTITY_NAME, USER_ID), results.get(0));
  }

  @Test
  public void registerUser_emptydUserId_throwIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> dataAccessor.registerUser(""));
  }

  @Test
  public void registerUser_nullUserId_throwIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> dataAccessor.registerUser(null));
  }

  @Test
  public void registerUser_alreadyRegistered_throwIllegalArgumentException() {
    // This test assumes that "registerUser" adds the user to the system, and makes sure that
    // the same user can't be added to the system more than once.
    dataAccessor.registerUser(USER_ID);

    assertThrows(IllegalArgumentException.class, () -> dataAccessor.registerUser(USER_ID));
  }

  @Test
  public void updateUserFeedback_userChosePlace_UpdateFeedback() {
    String chosenPlace = PLACE_ID_1;
    ImmutableList<String> places = ImmutableList.of(chosenPlace, PLACE_ID_2, PLACE_ID_3);

    dataAccessor.updateUserFeedback(
        buildUserFeedback(USER_ID, chosenPlace, places, false /**tried again*/));

    List<Entity> results = getRecommendationEntitiesByUserId(USER_ID);
    assertEquals(places.size(), results.size()); // An entity for each place.
    for (Entity entity : results) {
      String placeId = (String) entity.getProperty(DataAccessor.PLACE_ID_PROPERTY);
      assertTrue(places.contains(placeId));
      assertFalse((boolean) entity.getProperty(DataAccessor.TRY_AGAIN_PROPERTY));
      if (placeId == chosenPlace) {
        assertTrue((boolean) entity.getProperty(DataAccessor.CHOSEN_PROPERTY));
      }
    }
  }

  @Test
  public void updateUserFeedback_userTriedAgain_UpdateFeedback() {
    ImmutableList<String> places = ImmutableList.of(PLACE_ID_1, PLACE_ID_2, PLACE_ID_1);

    dataAccessor.updateUserFeedback(
        buildUserFeedback(USER_ID, null /**chosen place*/, places, true /**tried again*/));

    List<Entity> results = getRecommendationEntitiesByUserId(USER_ID);
    assertEquals(places.size(), results.size()); // An entity for each place.
    for (Entity entity : results) {
      assertTrue(places.contains((String) entity.getProperty(DataAccessor.PLACE_ID_PROPERTY)));
      assertTrue((boolean) entity.getProperty(DataAccessor.TRY_AGAIN_PROPERTY));
    }
  }

  @Test
  public void updateUserFeedback_noPlacesRecommended_noEntities() {
    ImmutableList<String> places = ImmutableList.of();

    dataAccessor.updateUserFeedback(
        buildUserFeedback(USER_ID, null /**chosen place*/, places, true /**tried again*/));

    List<Entity> results = getRecommendationEntitiesByUserId(USER_ID);
    assertEquals(0, results.size());
  }

  @Test
  public void getUserPlacesHistory_nullUser_throwIllegalArgumentException() {
    String userId = null;

    assertThrows(IllegalArgumentException.class,
        () -> dataAccessor.getPlacesRecommendedToUser(userId, true));
    assertThrows(IllegalArgumentException.class,
        () -> dataAccessor.getPlacesRecommendedToUser(userId, false));
  }

  @Test
  public void getUserPlacesHistory_emptyUser_throwIllegalArgumentException() {
    String userId = "";

    assertThrows(IllegalArgumentException.class,
        () -> dataAccessor.getPlacesRecommendedToUser(userId, true));
    assertThrows(IllegalArgumentException.class,
        () -> dataAccessor.getPlacesRecommendedToUser(userId, false));
  }

  @Test
  public void getUserPlacesHistory_allPlaces_getAllPlaces() {
    datastoreService.put(createRecomEntityByProperties(
      USER_ID, PLACE_ID_1, false /** chosen */, false /** tried again */));
    datastoreService.put(createRecomEntityByProperties(USER_ID, PLACE_ID_2, false /** chosen */,
        false /** tried again */));
    // Purposely add the same place IDs again, to make sure we get the places only once.
    // Once making the familiar place chosen, and once leaving in unchosen.
    datastoreService.put(createRecomEntityByProperties(
      USER_ID, PLACE_ID_1, true /** chosen */, false /** tried again */));
    datastoreService.put(createRecomEntityByProperties(USER_ID, PLACE_ID_2, false /** chosen */,
        false /** tried again */));

    ImmutableList<String> results =
        dataAccessor.getPlacesRecommendedToUser(USER_ID, false /** only places user chose */);

    assertEquals(2, results.size());
    assertTrue(results.contains(PLACE_ID_1));
    assertTrue(results.contains(PLACE_ID_2));
  }

  @Test
  public void getUserPlacesHistory_onlyChosenPlaces_getAllPlaces() {
    datastoreService.put(createRecomEntityByProperties(
      USER_ID, PLACE_ID_1, true /** chosen */, false /** tried again */));
    datastoreService.put(createRecomEntityByProperties(USER_ID, PLACE_ID_2, false /** chosen */,
        false /** tried again */));
    // Purposely add the same place IDs again, unchosen, to make sure it doesn't affect the results.
    datastoreService.put(createRecomEntityByProperties(
      USER_ID, PLACE_ID_1, false /** chosen */, false /** tried again */));
    datastoreService.put(createRecomEntityByProperties(USER_ID, PLACE_ID_2, false /** chosen */,
        false /** tried again */));

    ImmutableList<String> results =
        dataAccessor.getPlacesRecommendedToUser(USER_ID, true /** only places user chose */);

    assertEquals(1, results.size());
    assertTrue(results.contains(PLACE_ID_1));
  }

  private Entity createRecomEntityByProperties(String userId, String placeId, boolean chosen,
      boolean tryAgain) {
    Entity recommendationEntity = new Entity(DataAccessor.RECOMMENDATION_ENTITY_KIND);
    recommendationEntity.setProperty(DataAccessor.USER_ID_PROPERTY, userId);
    recommendationEntity.setProperty(DataAccessor.PLACE_ID_PROPERTY, placeId);
    recommendationEntity.setProperty(DataAccessor.CHOSEN_PROPERTY, chosen);
    recommendationEntity.setProperty(DataAccessor.TRY_AGAIN_PROPERTY, tryAgain);
    recommendationEntity.setProperty(DataAccessor.TIME_PROPERTY, System.currentTimeMillis());
    return recommendationEntity;
  }

  private PreparedQuery createPreparedQueryByUserIdAsKey(String userId) {
    Key userIdKey = KeyFactory.createKey(DataAccessor.USER_ENTITY_NAME, userId);
    Filter userIdFilter = new Query.FilterPredicate(
        Entity.KEY_RESERVED_PROPERTY,
        FilterOperator.EQUAL,
        userIdKey);
    Query query = new Query(DataAccessor.USER_ENTITY_NAME).setFilter(userIdFilter).setKeysOnly();
    return datastoreService.prepare(query);
  }

  private List<Entity> getRecommendationEntitiesByUserId(String userId) {
    return datastoreService.prepare(
        new Query(DataAccessor.RECOMMENDATION_ENTITY_KIND)
            .setFilter(new Query.FilterPredicate("UserId", FilterOperator.EQUAL, userId))
    ).asList(FetchOptions.Builder.withDefaults());
  }

  private UserFeedback buildUserFeedback(String userId, String chosenPlace,
      ImmutableList<String> places, boolean userTriedAgain) {
    UserFeedback.Builder temp = UserFeedback
        .builder()
        .setUserId(userId)
        .setPlacesRecommendedToUser(places)
        .setUserTriedAgain(userTriedAgain);
    return chosenPlace == null ? temp.build() : temp.setPlaceUserChose(chosenPlace).build();
  }
}
