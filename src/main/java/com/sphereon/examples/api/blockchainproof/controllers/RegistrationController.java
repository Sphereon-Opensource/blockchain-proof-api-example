package com.sphereon.examples.api.blockchainproof.controllers;

import com.sphereon.examples.api.blockchainproof.enums.UploadMethod;
import com.sphereon.libs.authentication.api.TokenRequest;
import com.sphereon.sdk.blockchain.proof.api.RegistrationApi;
import com.sphereon.sdk.blockchain.proof.handler.ApiException;
import com.sphereon.sdk.blockchain.proof.model.ContentRequest;
import com.sphereon.sdk.blockchain.proof.model.RegisterContentResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
public class RegistrationController {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final TokenRequest tokenRequester;
    private final RegistrationApi registrationApi;
    private final HashingController hashingController;

    @Value("${sphereon.blockchain-proof-api.upload-method}")
    private UploadMethod uploadMethod;


    public RegistrationController(final TokenRequest tokenRequester,
                                  final RegistrationApi registrationApi,
                                  final HashingController hashingController) {
        this.tokenRequester = tokenRequester;
        this.registrationApi = registrationApi;
        this.hashingController = hashingController;
    }


    public void registerFile(final String configName,
                             final File targetFile) {
        tokenRequester.execute();

        try {
            switch (uploadMethod) {
                case STREAM:
                    registerUsingStream(configName, targetFile);
                    break;
                case BASE64_CONTENT:
                    registerUsingContent(configName, targetFile);
                    break;
            }
        } catch (ApiException e) {
            throw new RuntimeException(String.format("Registration request failed with http code %d and message: %s. The response body was %n%s",
                    e.getCode(), e.getMessage(), e.getResponseBody()));
        }
    }


    private void registerUsingStream(final String configName,
                                     final File targetFile) throws ApiException {
        final var response = registrationApi.registerUsingStream(configName, targetFile, targetFile.getName(), null, null,
                null, null);
        logResponse(targetFile, response);
    }


    private void registerUsingContent(final String configName,
                                      final File targetFile) throws ApiException {
        final var response = registrationApi.registerUsingContent(configName, buildExistence(targetFile), null, null,
                null, null);
        logResponse(targetFile, response);
    }


    private ContentRequest buildExistence(final File targetFile) {
        try {
            return new ContentRequest()
                    .hashProvider(ContentRequest.HashProviderEnum.CLIENT) // Using this method you don't actually send your content to the Sphereon cloud
                    .content(hashingController.hashFileToByteArray(targetFile));
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while hashing file " + targetFile.getAbsolutePath());
        }
    }


    private void logResponse(final File targetFile,
                             final RegisterContentResponse response) {
        log.info(String.format("Registration result for file %s:%n%s", targetFile.getName(), response));
        log.info("Please note that it can take up to 10 minutes before this record is fully anchored on the Blockchain.");
    }
}
