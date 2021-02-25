package com.sphereon.examples.api.blockchainproof.config;

import com.sphereon.libs.authentication.api.AuthenticationApi;
import com.sphereon.libs.authentication.api.TokenRequest;
import com.sphereon.libs.authentication.api.TokenResponse;
import com.sphereon.libs.authentication.api.config.ApiConfiguration;
import com.sphereon.libs.authentication.api.config.PersistenceType;
import com.sphereon.sdk.blockchain.proof.api.ConfigurationApi;
import com.sphereon.sdk.blockchain.proof.api.RegistrationApi;
import com.sphereon.sdk.blockchain.proof.api.VerificationApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ApiConfig {
    private static final String APPLICATION_NAME = "BCPDEMO";
    private static final int TOKEN_VALIDITY_SECONDS = 5400;
    private static final int TIMEOUT = 58000;

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(ApiConfig.class);


    @Bean
    AuthenticationApi authenticationApi(@Value("${sphereon.authentication.client-id}") String clientId) {
        final ApiConfiguration config = new ApiConfiguration.Builder()
                .withApplication(APPLICATION_NAME)
                .withPersistenceType(StringUtils.isEmpty(clientId) ? PersistenceType.SYSTEM_ENVIRONMENT : PersistenceType.DISABLED)
                .build();
        return new AuthenticationApi.Builder()
                .withConfiguration(config)
                .build();
    }


    @Bean
    TokenRequest tokenRequester(@Autowired AuthenticationApi authenticationApi,
                                @Value("${sphereon.authentication.client-id}") String clientId,
                                @Value("${sphereon.authentication.client-secret}") String clientSecret) {
        return authenticationApi.requestToken()
                .withConsumerKey(clientId)
                .withConsumerSecret(clientSecret)
                .withValidityPeriod(TOKEN_VALIDITY_SECONDS)
                .build();
    }


    @Bean
    public ConfigurationApi configurationApi(TokenRequest tokenRequester) {
        ConfigurationApi configurationApi = new ConfigurationApi();
        configureApiClient(tokenRequester, configurationApi.getApiClient());
        return configurationApi;
    }


    @Bean
    public RegistrationApi registrationApi(TokenRequest tokenRequester) {
        RegistrationApi registrationApi = new RegistrationApi();
        configureApiClient(tokenRequester, registrationApi.getApiClient());
        return registrationApi;
    }


    @Bean
    public VerificationApi verificationApi(TokenRequest tokenRequester) {
        VerificationApi verificationApi = new VerificationApi();
        configureApiClient(tokenRequester, verificationApi.getApiClient());
        return verificationApi;
    }


    private void configureApiClient(final TokenRequest tokenRequester,
                                    final com.sphereon.sdk.blockchain.proof.handler.ApiClient apiClient) {
        apiClient.setConnectTimeout(TIMEOUT);
        apiClient.getHttpClient().setReadTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        apiClient.getHttpClient().setWriteTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        apiClient.setAccessToken(tokenRequester.execute().getAccessToken());
        tokenRequester.addTokenResponseListener(new TokenRequest.TokenResponseListener() {
            @Override
            public void tokenResponse(TokenResponse tokenResponse) {
                apiClient.setAccessToken(tokenResponse.getAccessToken());
            }


            @Override
            public void exception(Throwable throwable) {
                log.error("An error occurred while renewing token for the Blockchain Proof API", throwable);
            }
        });
    }
}
