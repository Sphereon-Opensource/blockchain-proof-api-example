package com.sphereon.examples.api.blockchainproof.controllers;

import com.sphereon.examples.api.blockchainproof.enums.UploadMethod;
import com.sphereon.examples.api.blockchainproof.config.ApiConfig;
import com.sphereon.libs.authentication.api.TokenRequest;
import com.sphereon.sdk.blockchain.proof.api.VerificationApi;
import com.sphereon.sdk.blockchain.proof.handler.ApiException;
import com.sphereon.sdk.blockchain.proof.model.ContentRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
public class VerificationController {

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(ApiConfig.class);

    private final TokenRequest tokenRequester;
    private final VerificationApi verificationApi;
    private final HashingController hashingController;

    @Value("${sphereon.blockchain-proof-api.upload-method}")
    private UploadMethod uploadMethod;


    public VerificationController(final TokenRequest tokenRequester,
                                  final VerificationApi verificationApi,
                                  final HashingController hashingController) {
        this.tokenRequester = tokenRequester;
        this.verificationApi = verificationApi;
        this.hashingController = hashingController;
    }


    public void verifyFile(final String configName,
                           final File targetFile) {
        tokenRequester.execute();

        try {
            switch (uploadMethod) {
                case STREAM:
                    verifyUsingStream(configName, targetFile);
                    break;
                case CONTENT:
                    verifyUsingContent(configName, targetFile);
                    break;
            }
        } catch (ApiException e) {
            throw new RuntimeException(String.format("Verification request failed with http code %d and message: %s. The response body was %n%s",
                    e.getCode(), e.getMessage(), e.getResponseBody()));
        }
    }


    private void verifyUsingStream(final String configName,
                                   final File targetFile) throws ApiException {
        final var response = verificationApi.verifyUsingStream(configName, targetFile, null, null, null,
                null, null);
        logResponse(targetFile, response);
    }


    private void verifyUsingContent(final String configName,
                                    final File targetFile) throws ApiException {
        final var response = verificationApi.verifyUsingContent(configName, buildExistence(targetFile), null, null,
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
                             final com.sphereon.sdk.blockchain.proof.model.VerifyContentResponse response) {
        log.info(String.format("Verification result for file %s:%n%s", targetFile.getName(), response));
    }
}
