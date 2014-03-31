package com.bonitasoft.engine.authentication.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

import org.bonitasoft.engine.authentication.AuthenticationConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bonitasoft.engine.authentication.impl.cas.CASCallbackHandlerDelegate;
import com.bonitasoft.engine.authentication.impl.cas.CASUtils;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationCallbackHandlerTest {

    AuthenticationCallbackHandler authenticationCallbackHandler;

    @Mock
    CASCallbackHandlerDelegate casCallbackHandlerDelegate;

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticationCallbackHandler() throws Exception {
        new AuthenticationCallbackHandler(null);
    }

    @Test
    public void testHandlePassword() throws Exception {
        String password = "password";
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        credentials.put(AuthenticationConstants.BASIC_PASSWORD, password);
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        PasswordCallback pc = mock(PasswordCallback.class);

        authenticationCallbackHandler.handlePassword(pc);
        verify(pc, times(1)).setPassword(password.toCharArray());

    }

    @Test
    public void testHandleUsername() throws Exception {
        String username = "username";
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        credentials.put(AuthenticationConstants.BASIC_USERNAME, username);
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        NameCallback pc = mock(NameCallback.class);

        authenticationCallbackHandler.handleName(pc);
        verify(pc, times(1)).setName(username);

    }

    @Test
    public void testHandleUsernameEmpty() throws Exception {
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        NameCallback pc = mock(NameCallback.class);

        authenticationCallbackHandler.handleName(pc);
        verify(pc, times(0)).setName(anyString());

    }

    @Test
    public void testHandleTicketShouldReturnTicket() throws Exception {
        String ticket = "service";
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        credentials.put(AuthenticationConstants.CAS_TICKET, ticket);
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        when(casCallbackHandlerDelegate.getCASTicket(credentials)).thenReturn(ticket);

        PasswordCallback pc = mock(PasswordCallback.class);
        when(pc.getPrompt()).thenReturn(AuthenticationConstants.CAS_TICKET);

        authenticationCallbackHandler.handlePassword(pc);
        verify(pc, times(1)).setPassword(ticket.toCharArray());
    }

    @Test
    public void testHandleTicketEmptyShouldReturnNoTicket() throws Exception {
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        PasswordCallback pc = mock(PasswordCallback.class);
        when(pc.getPrompt()).thenReturn(AuthenticationConstants.CAS_TICKET);

        authenticationCallbackHandler.handlePassword(pc);
        verify(pc, times(0)).setPassword(any(char[].class));
    }

    @Test
    public void testHandleServiceShouldReturnService() throws Exception {
        String service = "service";
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        credentials.put(AuthenticationConstants.CAS_SERVICE, service);
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        when(casCallbackHandlerDelegate.getCASService(credentials)).thenReturn(service);
        NameCallback nc = mock(NameCallback.class);
        when(nc.getPrompt()).thenReturn(AuthenticationConstants.CAS_SERVICE);

        authenticationCallbackHandler.handleName(nc);
        verify(nc, times(1)).setName(service);
    }

    @Test
    public void testHandleServiceEmptyShouldReturnNoService() throws Exception {
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        NameCallback nc = mock(NameCallback.class);
        when(nc.getPrompt()).thenReturn(AuthenticationConstants.CAS_SERVICE);

        authenticationCallbackHandler.handleName(nc);
        verify(nc, times(0)).setName(anyString());
    }

    @Test
    public void testHandleServiceShouldThrowLicenseException() throws Exception {
        String service = "service";
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        credentials.put(AuthenticationConstants.CAS_SERVICE, service);
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        when(casCallbackHandlerDelegate.getCASService(credentials)).thenThrow(
                new IllegalStateException(CASUtils.THE_CAS_AUTHENTICATOR_IS_NOT_AN_ACTIVE_FEATURE));
        NameCallback nc = mock(NameCallback.class);
        when(nc.getPrompt()).thenReturn(AuthenticationConstants.CAS_SERVICE);

        try {
            authenticationCallbackHandler.handleName(nc);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage(CASUtils.THE_CAS_AUTHENTICATOR_IS_NOT_AN_ACTIVE_FEATURE);
            verify(nc, times(0)).setName(anyString());
            return;
        }
        fail();
    }

    @Test
    public void testHandleTicketShouldThrowLicenseException() throws Exception {
        String ticket = "ticket";
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        credentials.put(AuthenticationConstants.CAS_SERVICE, ticket);
        authenticationCallbackHandler = new AuthenticationCallbackHandler(credentials);
        authenticationCallbackHandler.casCallbackHandlerDelegate = casCallbackHandlerDelegate;
        when(casCallbackHandlerDelegate.getCASTicket(credentials)).thenThrow(
                new IllegalStateException(CASUtils.THE_CAS_AUTHENTICATOR_IS_NOT_AN_ACTIVE_FEATURE));
        PasswordCallback pc = mock(PasswordCallback.class);
        when(pc.getPrompt()).thenReturn(AuthenticationConstants.CAS_TICKET);

        try {
            authenticationCallbackHandler.handlePassword(pc);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage(CASUtils.THE_CAS_AUTHENTICATOR_IS_NOT_AN_ACTIVE_FEATURE);
            verify(pc, times(0)).setPassword(any(char[].class));
            return;
        }
        fail();
    }
}
