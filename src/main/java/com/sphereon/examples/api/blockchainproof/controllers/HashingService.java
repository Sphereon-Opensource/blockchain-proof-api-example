
/**
 * This service uses Sphereon's easy-blockchain-lib library to do the file hashing locally.
 */

package com.sphereon.examples.api.blockchainproof.controllers;

import com.sphereon.libs.blockchain.commons.Digest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class HashingService {

    public String hashFileToString(final File file) throws IOException {
        try (final var inputStream = new FileInputStream(file)) {
            return Digest.getInstance().getHashAsString(Digest.Algorithm.SHA_256, inputStream, Digest.Encoding.HEX);
        }
    }


    public byte[] hashFileToByteArray(final File file) throws IOException {
        return hashFileToString(file).getBytes();
    }
}
