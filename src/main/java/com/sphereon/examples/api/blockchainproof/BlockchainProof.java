package com.sphereon.examples.api.blockchainproof;

import com.sphereon.examples.api.blockchainproof.controllers.ConfigurationController;
import com.sphereon.examples.api.blockchainproof.controllers.RegistrationController;
import com.sphereon.examples.api.blockchainproof.controllers.VerificationController;
import com.sphereon.examples.api.blockchainproof.enums.Operation;
import org.apache.commons.lang3.StringUtils;
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

    public static final String ARG_CONFIG_NAME = "config-name";

    private final ConfigurationController configurationController;
    private final ObjectFactory<RegistrationController> registrationControllers;
    private final ObjectFactory<VerificationController> verificationControllers;
    private final ApplicationArguments applicationArguments;

    @Value("${sphereon.blockchain-proof-api.configuration-name}")
    private String configName;

    private Operation operation;
    private File targetFile;


    public BlockchainProof(final ConfigurationController configurationController,
                           final ObjectFactory<RegistrationController> registrationControllers,
                           final ObjectFactory<VerificationController> verificationControllers,
                           final ApplicationArguments applicationArguments) {
        this.configurationController = configurationController;
        this.registrationControllers = registrationControllers;
        this.verificationControllers = verificationControllers;
        this.applicationArguments = applicationArguments;
    }


    public static void main(String[] args) {
        new DefaultApplicationArguments(args);
        SpringApplication.run(BlockchainProof.class, args);
    }


    @Override
    public void run(final String... args) throws Exception {
        try {
            readApplicationArguments();
            configurationController.checkConfiguration(selectConfigName());
            switch (operation) {
                case REGISTER:
                    registrationControllers.getObject().registerFile(configName, targetFile);
                    break;
                case VERIFY:
                    verificationControllers.getObject().verifyFile(configName, targetFile);
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
                    throw new IllegalArgumentException(String.format("File %s does not exists!", filePath));
                }
            }
            return;
        }
        printErrorAndExit();
    }


    private void printErrorAndExit() {
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
}
