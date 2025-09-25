package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.config.SecurityConfiguration;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.AppUserService;
import com.sep.mmms_backend.testing_tools.SerializerDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//IMPORTANT NOTE: The following tests tightly couples Exception objects .getMessage() method. If the message content were to change in the future as a part of framework change, the test itself will fail. You may need to read the logs and then update the 'appended string'

@WebMvcTest(controllers = {LoginLogoutController.class})
@Import(SecurityConfiguration.class)
@Slf4j
public class LoginLogoutControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AppUserService appUserService;

    AppUser user;

    //1) tests when the provided username does not exist in the database
    @Test
    @WithAnonymousUser
    public void LoginLogoutController_TryLogin_Returns_UNAUTHORIZED() throws Exception {
        String username = "username";
        String password = "password";

        Mockito.when(appUserService.loadUserByUsername("username")).thenReturn(null);

        String credentials = Base64.getEncoder().encodeToString((username+":"+password).getBytes());

        MvcResult result = mockMvc.perform(get("/api/login").header(HttpHeaders.AUTHORIZATION, "Basic " + credentials).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized()).andReturn();

        Response response = SerializerDeserializer.deserialize(result.getResponse().getContentAsString());

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getMessage()).isNotNull();
        Assertions.assertThat(response.getMessage().startsWith(ResponseMessages.AUTHENTICATION_FAILED.toString() + " Bad Credentials"));
    }

    //2) tests when the provided username does exists in the database, but password is wrong

    @Test
    @WithAnonymousUser
    public void LoginLogoutController_TryLogin_Returns_UNAUTHORIZED_2() throws Exception {
        String username = "username";
        String password = "password";

        Mockito.when(appUserService.loadUserByUsername("username")).thenReturn(AppUser.builder().username("username").password("{noop} notpassword").build());

        String credentials = Base64.getEncoder().encodeToString((username+":"+password).getBytes());

        MvcResult result = mockMvc.perform(get("/api/login").header(HttpHeaders.AUTHORIZATION, "Basic " + credentials).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized()).andReturn();



        Response response = SerializerDeserializer.deserialize(result.getResponse().getContentAsString());

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getMessage()).isNotNull();
        Assertions.assertThat(response.getMessage().startsWith(ResponseMessages.AUTHENTICATION_FAILED.toString() + " Bad Credentials"));
    }


    //3 tests the success case where both the provided username and password is present in the database
    @Test
    @WithAnonymousUser
    public void LoginLogoutController_TryLogin_Returns_OK() throws Exception {
        String username = "username";
        String password = "password";

        Mockito.when(appUserService.loadUserByUsername("username")).thenReturn(AppUser.builder().username("username").password("{noop}password").build());

        String credentials = Base64.getEncoder().encodeToString((username+":"+password).getBytes());

        MvcResult result = mockMvc.perform(get("/api/login").header(HttpHeaders.AUTHORIZATION, "Basic " + credentials).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();



        Response response = SerializerDeserializer.deserialize(result.getResponse().getContentAsString());

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getMessage()).isNotNull();
        Assertions.assertThat(response.getMessage().startsWith(ResponseMessages.LOGIN_SUCCESSFUL.toString()));
    }


    //4) tests the case when an anonymous user tries to logout
    @Test
    @WithAnonymousUser
    public void LoginLogoutController_Logout_Returns_FORBIDDEN() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/logout").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized()).andReturn();

        Response response = SerializerDeserializer.deserialize(result.getResponse().getContentAsString());

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getMessage()).isNotNull();
        Assertions.assertThat(response.getMessage().startsWith(ResponseMessages.ACCESS_DENIED.toString() + " Full authentication is required to access this resource"));
    }

    //5) tests the case when a logged in user tries to logout
    @Test
    @WithMockUser(username="username")
    public void LoginLogoutController_Logout_Returns_OK() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/logout").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        Response response = SerializerDeserializer.deserialize(result.getResponse().getContentAsString());

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getMessage()).isNotNull();
        Assertions.assertThat(response.getMessage().startsWith(ResponseMessages.LOGOUT_SUCCESSFUL.toString()));
    }

}
