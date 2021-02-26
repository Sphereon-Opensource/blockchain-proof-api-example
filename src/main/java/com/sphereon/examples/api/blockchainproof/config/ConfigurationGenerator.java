package com.sphereon.examples.api.blockchainproof.config;

import com.sphereon.sdk.blockchain.proof.model.ChainSettings;
import com.sphereon.sdk.blockchain.proof.model.CreateConfigurationRequest;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sphereon.sdk.blockchain.proof.model.ChainSettings.ContentRegistrationChainTypesEnum.PER_HASH_PROOF_CHAIN;
import static com.sphereon.sdk.blockchain.proof.model.ChainSettings.ContentRegistrationChainTypesEnum.SINGLE_PROOF_CHAIN;
import static com.sphereon.sdk.blockchain.proof.model.CreateConfigurationRequest.AccessModeEnum;

@Component
public class ConfigurationGenerator {

    public CreateConfigurationRequest generateDefaultConfiguration(final String configName) {
        return new CreateConfigurationRequest()
                .name(configName)
                .context("factom") // Register in the Factom ledger
                .accessMode(AccessModeEnum.PRIVATE)
                .initialSettings(buildChainSettings());
    }


    private ChainSettings buildChainSettings() {
        return new ChainSettings()
                .hashAlgorithm(ChainSettings.HashAlgorithmEnum._256)
                // Enabled both per-hash & single proof chains. Please see chapter 3 of the documentation for more information.
                .contentRegistrationChainTypes(List.of(PER_HASH_PROOF_CHAIN, SINGLE_PROOF_CHAIN));
    }
}
