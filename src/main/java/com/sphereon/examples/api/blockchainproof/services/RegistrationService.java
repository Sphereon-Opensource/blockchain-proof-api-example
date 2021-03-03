
/**
 * This service uses Sphereon's REST API using a Swagger generated Java SDK to call the registration endpoints.
 */

package com.sphereon.examples.api.blockchainproof.services;

import com.sphereon.examples.api.blockchainproof.enums.HashProviderMode;
import com.sphereon.libs.authentication.api.TokenRequest;
import com.sphereon.sdk.blockchain.proof.api.RegistrationApi;
import com.sphereon.sdk.blockchain.proof.handler.ApiException;
import com.sphereon.sdk.blockchain.proof.model.ContentRequest;
import com.sphereon.sdk.blockchain.proof.model.RegisterContentResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class RegistrationService {

    private final TokenRequest tokenRequester;
    private final RegistrationApi registrationApi;
    private final HashingService hashingService;

    @Value("${sphereon.blockchain-proof-api.hash-provider-mode}")
    private HashProviderMode hashProviderMode;


    public RegistrationService(final TokenRequest tokenRequester,
                               final RegistrationApi registrationApi,
                               final HashingService hashingService) {
        this.tokenRequester = tokenRequester;
        this.registrationApi = registrationApi;
        this.hashingService = hashingService;
    }


    public RegisterContentResponse registerFile(final String configName,
                                                final File targetFile) {
        tokenRequester.execute(); // Fetch a new access token if not there yet or is about to expire

        try {
            switch (hashProviderMode) {
                case SERVER_SIDE:
                    // Send the content to the Sphereon cloud to let the hashing take place there.
                    return registerUsingStream(configName, targetFile);
                case CLIENT_SIDE:
                    // We can only register client side generated hashes using the registerUsingContent operation
                    return registerUsingContent(configName, targetFile);
            }
        } catch (ApiException e) {
            throw new RuntimeException(String.format("Registration request failed with http code %d and message: %s. The response body was %n%s",
                    e.getCode(), e.getMessage(), e.getResponseBody()));
        }
        throw new NotImplementedException("hashProviderMode " + hashProviderMode);
    }


    private RegisterContentResponse registerUsingStream(final String configName,
                                                        final File targetFile) throws ApiException {
        return registrationApi.registerUsingStream(configName, targetFile, targetFile.getName(), null, null,
                null, null);
    }


    private RegisterContentResponse registerUsingContent(final String configName,
                                                         final File targetFile) throws ApiException {
        return registrationApi.registerUsingContent(configName, buildExistence(targetFile), null, null,
                null, null);
    }


    private ContentRequest buildExistence(final File targetFile) {
        try {
            return new ContentRequest()
                    /* When using HashProviderEnum.CLIENT you don't actually send your content to the Sphereon cloud,
                        but will have to provide the hash. */
                    .hashProvider(ContentRequest.HashProviderEnum.CLIENT)
                    .content(hashingService.hashFileToByteArray(targetFile));
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while hashing file " + targetFile.getAbsolutePath());
        }
    }
}
