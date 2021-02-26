package com.sphereon.examples.api.blockchainproof;

import com.sphereon.examples.api.blockchainproof.controllers.ConfigurationService;
import com.sphereon.examples.api.blockchainproof.controllers.RegistrationService;
import com.sphereon.examples.api.blockchainproof.controllers.VerificationService;
import com.sphereon.examples.api.blockchainproof.enums.Operation;
import com.sphereon.sdk.blockchain.proof.model.RegisterContentResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class BlockchainProof implements CommandLineRunner {

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(BlockchainProof.class);

    public static final String ARG_CONFIG_NAME = "config-name";

    private final ConfigurationService configurationService;
    private final ObjectFactory<RegistrationService> registrationServiceFactory;
    private final ObjectFactory<VerificationService> verificationServiceFactory;
    private final ApplicationArguments applicationArguments;

    @Value("${sphereon.blockchain-proof-api.configuration-name}")
    private String configName;

    private Operation operation;
    private File targetFile;


    public BlockchainProof(final ConfigurationService configurationService,
                           final ObjectFactory<RegistrationService> registrationServiceFactory,
                           final ObjectFactory<VerificationService> verificationServiceFactory,
                           final ApplicationArguments applicationArguments) {
        this.configurationService = configurationService;
        this.registrationServiceFactory = registrationServiceFactory;
        this.verificationServiceFactory = verificationServiceFactory;
        this.applicationArguments = applicationArguments;
    }


    public static void main(String[] args) {
        new DefaultApplicationArguments(args);
        SpringApplication.run(BlockchainProof.class, args);
    }


    @Override
    public void run(final String... args) {
        try {
            readApplicationArguments();

            // First check if the configured configuration name is already known for the current account. Create it when it's not.
            configurationService.checkConfiguration(selectConfigName());

            // Execute to requested operation
            switch (operation) {
                case REGISTER:
                    final var registerContentResponse = registrationServiceFactory.getObject()
                            .registerFile(configName, targetFile);
                    logRegisterContentResponse(targetFile, registerContentResponse);
                    break;
                case VERIFY:
                    final var verifyContentResponse = verificationServiceFactory.getObject()
                            .verifyFile(configName, targetFile);
                    logVerifyContentResponse(targetFile, verifyContentResponse);
                    break;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    private void readApplicationArguments() {
        if (applicationArguments.getNonOptionArgs().size() >= 2) {
            final String operationArg = applicationArguments.getNonOptionArgs().get(0);
            if (StringUtils.equalsAnyIgnoreCase(operationArg, "register", "verify")) {
                this.operation = Operation.fromString(operationArg);
                final var filePath = applicationArguments.getNonOptionArgs().get(1);
                this.targetFile = new File(filePath);
                if (!targetFile.exists()) {
                    throw new IllegalArgumentException(String.format("File %s does not exist!", filePath));
                }
            }
            return;
        }
        printArgumentsMessageAndExit();
    }


    private void printArgumentsMessageAndExit() {
        System.err.println("This program needs to be called with a minimum of two arguments:"
                + System.lineSeparator() + "\tregister <file path> [--config-name <my-config-name>]"
                + System.lineSeparator() + "\tor"
                + System.lineSeparator() + "\tverify <file path> [--config-name <my-config-name>]");
        System.exit(-1);
    }


    private String selectConfigName() {
        if (applicationArguments.containsOption("config-name")) {
            final var configNameValues = applicationArguments.getOptionValues(ARG_CONFIG_NAME);
            if (configNameValues.isEmpty()) {
                throw new IllegalArgumentException("Config name was not specified!");
            }
            configName = configNameValues.get(0);
            if (StringUtils.isBlank(configName)) {
                throw new IllegalArgumentException("Config name was not specified!");
            }
        }
        return configName;
    }


    private void logRegisterContentResponse(final File targetFile,
                                            final RegisterContentResponse response) {
        log.info(String.format("Registration result for file %s:%n%s", targetFile.getName(), response));
        log.info("Please note that it can take up to 10 minutes before this record is fully anchored on the Blockchain.");
    }


    private void logVerifyContentResponse(final File targetFile,
                                          final com.sphereon.sdk.blockchain.proof.model.VerifyContentResponse response) {
        log.info(String.format("Verification result for file %s:%n%s", targetFile.getName(), response));
    }
}
