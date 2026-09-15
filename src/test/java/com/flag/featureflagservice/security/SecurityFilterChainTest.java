package com.flag.featureflagservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.flag.featureflagservice.TestConstants.BEARER_PREFIX;
import static com.flag.featureflagservice.TestConstants.DELETE_URL;
import static com.flag.featureflagservice.TestConstants.LOGIN_URL;
import static com.flag.featureflagservice.TestConstants.MALFORMED_TOKEN;
import static com.flag.featureflagservice.TestConstants.PASSWORD;
import static com.flag.featureflagservice.TestConstants.PROTECTED_URL;
import static com.flag.featureflagservice.TestConstants.ROLE_ADMIN;
import static com.flag.featureflagservice.TestConstants.ROLE_VIEWER;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static com.flag.featureflagservice.TestConstants.WWW_AUTHENTICATE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the real {@code SecurityFilterChain} rather than a mocked filter, so that filter
 * ordering, the authorization rules and the JSON error contract are all covered end to end.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security filter chain")
class SecurityFilterChainTest {

    private static final String ENVIRONMENT_QUERY = "environmentId";
    private static final String ENVIRONMENT_ID = "1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Given no credentials, when calling a protected endpoint, then JSON 401 without a Basic challenge")
    void givenNoCredentials_whenProtectedEndpoint_thenJsonUnauthorizedWithoutChallenge() throws Exception {
        mockMvc.perform(get(PROTECTED_URL).param(ENVIRONMENT_QUERY, ENVIRONMENT_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Authentication required"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(header().doesNotExist(WWW_AUTHENTICATE));
    }

    @Test
    @DisplayName("Given a valid admin token, when calling a protected endpoint, then authentication succeeds")
    void givenValidAdminToken_whenProtectedEndpoint_thenAuthenticationSucceeds() throws Exception {
        String token = jwtService.issue(USERNAME, ROLE_ADMIN);

        // 404 rather than 200: the chain authenticated the caller and the controller ran,
        // then failed on the absent application — which is the point being proved.
        mockMvc.perform(get(PROTECTED_URL).param(ENVIRONMENT_QUERY, ENVIRONMENT_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given a viewer token, when deleting, then JSON 403 from the access denied handler")
    void givenViewerToken_whenDelete_thenJsonForbidden() throws Exception {
        String token = jwtService.issue(USERNAME, ROLE_VIEWER);

        mockMvc.perform(delete(DELETE_URL).header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Given an unparseable token, when calling a protected endpoint, then 401")
    void givenInvalidToken_whenProtectedEndpoint_thenUnauthorized() throws Exception {
        mockMvc.perform(get(PROTECTED_URL).param(ENVIRONMENT_QUERY, ENVIRONMENT_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + MALFORMED_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Given no credentials, when logging in, then the endpoint is reachable")
    void givenNoCredentials_whenLogin_thenEndpointIsReachable() throws Exception {
        mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + USERNAME + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Given Basic credentials, when calling a protected endpoint, then Basic still authenticates")
    void givenBasicCredentials_whenProtectedEndpoint_thenBasicStillWorks() throws Exception {
        mockMvc.perform(get(PROTECTED_URL).param(ENVIRONMENT_QUERY, ENVIRONMENT_ID)
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound());
    }
}
