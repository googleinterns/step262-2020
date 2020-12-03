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

package com.google.sps.servlets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.DataAccessor;
import com.google.sps.data.UserVerifier;

@RunWith(JUnit4.class)
public class RegistrationServletTest {

 private static final HttpServletRequest REQUEST = mock(HttpServletRequest.class);
 private static final HttpServletResponse RESPONSE = mock(HttpServletResponse.class);
 private RegistrationServlet servlet;
 private UserVerifier userVerifier;
 private DataAccessor dataAccessor;

 @Before
 public void setUp() throws Exception {
   servlet = new RegistrationServlet();
   userVerifier = mock(UserVerifier.class);
   dataAccessor = new DataAccessor();
   servlet.init(userVerifier, dataAccessor);
 }

 @Test
 public void doPost_validToken_registerUser() throws Exception {
  //  String userId = mockValidTokenAndReturnVerifiedUserId();

  //  servlet.doPost(REQUEST, RESPONSE);

  //  verify(dataAccessor).registerUser(userId);
 }

 public void doPost_invalidToken_notRegistering() throws Exception {
  //  // This is the behaviour when a token ID isn't valid
  //  when(userVerifier.verify(any(String.class))).thenReturn(null);

  //  servlet.doPost(REQUEST, RESPONSE);

  //  verify(dataAccessor, never()).registerUser(any(String.class));
 }

 public void doPost_validTokenAlreadyRegisteredUser_notRegistering() throws Exception {
  //  String userId = mockValidTokenAndReturnVerifiedUserId();
  //  when(dataAccessor.isRegistered(userId)).thenReturn(true);

  //  servlet.doPost(REQUEST, RESPONSE);

  //  verify(dataAccessor, never()).registerUser(any(String.class));
 }
}