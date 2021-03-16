# Getting Started

### Sphereon Store Account
To run this sample you will first need to register yourself in our API store. The following steps are required to get this example to work:
* Go to the [Sphereon API store](https://store.sphereon.com/store/) and click "Sign-Up"
* Once you have registered and signed in, go to menu "Applications" and either create a new entry or open the default one.
* Go to tab "Sandbox Keys" and create a Consumer Key and Secret. Make sure the "Client Credential" box s ticked.
* Copy the values in fields "Consumer Key" and "Consumer Secret" and save them for later.
* Go to menu "APIS" and select API "Blockchain-Proof-0.10" -> [DIRECT LINK](https://store.sphereon.com/store/apis/info?name=Blockchain-Proof&version=0.10&provider=admin)
* Under drop down list "Applications", choose the application name for which you just collected the consumer key & secret from, then click on "Subscribe"
* Go to menu "APIS" and select API "Easy-Blockchain-0.10" -> [DIRECT LINK](https://store.sphereon.com/store/apis/info?name=Easy-Blockchain&version=0.10&provider=admin)
* Under drop down list "Applications", choose the application name for which you just collected the consumer key & secret from, then click on "Subscribe"
* Optionally: If you want to use the public/private keys from the crypto keys API:
  * Go to menu "APIS" and select API "Crypto-Keys-0.9" -> [DIRECT LINK](https://store.sphereon.com/store/apis/info?name=Crypto-Keys&version=0.9&provider=admin)
  * Under drop down list "Applications", choose the application name for which you just collected the consumer key & secret from, then click on "Subscribe"
* Next the client id (Consumer Key) & client secret (Consumer Secret) properties for this application have to populated with the consumer key & secret values from the store. There are three locations this can be done:  
  1 As environment variables AUTHENTICATION_API_BCPDEMO_CONSUMER_KEY  AUTHENTICATION_API_BCPDEMO_CONSUMER_SECRET  
  2 In the application.properties file sphereon.authentication.client-id & sphereon.authentication.client-secret  
  3 As Java VM arguments -Dsphereon.authentication.client-id= & -Dsphereon.authentication.client-secret=


### Prerequisites
* JDK 11 or higher
* Maven 3.


### Building
This is a maven project which can be build with standard maven commands 
```
mvn package
```

### Running
To run the built artifact start a shell and to the blockchain-proof-api-example\target directory. (Or copy blockchain-proof.jar to a directory of your choice.)  
This project is a command line program which can be called as follows:
```
java -jar blockchain-proof.jar register <path-to-file>
java -jar blockchain-proof.jar verify <path-to-file>
```
or optionally with config name:
```
java -jar blockchain-proof.jar register <path-to-file> [--config-name <my-config-name>]
```
When no config name is supplied, it will take it from application.properties.

### Hash provider methods
The example implements two methods for generating the file hash, SERVER_SIDE & CLIENT_SIDE which can be configured in application.properties. 
(To override the application.properties settings after building, you can copy the file from the project's resources directory to config/application.properties relative of your working directory.)
```
sphereon.blockchain-proof-api.hash-provider-mode=SERVER_SIDE
```
In this mode the file to register or verify is uploaded our cloud and hashed. We do not store the file. 
A powerful extra feature you get in this mode is the support for hashing document content, meaning that if for instance someone opens then 
saves a PDF file, the new file will still validate as long as the content didn't change.

```
sphereon.blockchain-proof-api.hash-provider-mode=CLIENT_SIDE
```
In this mode the file to register or verify is hashed locally. No file content is actually sent over the network, only the hash.
This is the most secure option for documents containing private/confidential information. Content hashing is not available in this mode as
we don't get the content.


### Configuration
You create a configuration typically once. So normally you don't have to integrate the configuration endpoints in your application.
Adding and listing configurations could even be done through the [API console in our store](https://store.sphereon.com/store/apis/info?name=Blockchain-Proof&version=0.10&provider=admin). Documentation is available [here](https://docs.sphereon.com/api/blockchain-proof/0.10/html#_createconfiguration).

Below is an example configuration that one can use for testing purposes. Change the name of the configuration with a value of your own. Also change the base64 secret into another value.
Please note that using a static shared secret is fine for testing purposes. For production usage, we encourage to use a public/private keypair optionally protected by a Hardware Security Module using our crypto-keys API.
We are happy to help out with setting up a configuration for that.

The below example would create a configuration named 'my-config-name-here' with a fixed shared secret that has value 'secret' in base64.
It uses both a single proof chain, that will contain all blockchain entries of all files into a single chain and a per hash/file chain,
meaning a new chain per file/hash. It is up to the user to determine which type to use (both is fine).

```json
{
    "initialSettings": {
        "contentRegistrationChainTypes": [
            "PER_HASH_PROOF_CHAIN",
            "SINGLE_PROOF_CHAIN"
        ],
        "signatureSettings": {
            "base64Secret": "c2VjcmV0",
            "signatureType": "SECRET"
        },
        "version": 1,
        "hashAlgorithm": "SHA_256"
    },
    "context": "multichain",
    "name": "my-config-name-here",
    "contentExtractionSettings": {
        "binaryComparison": false,
        "contentExtraction": false,
        "minimumContentExtractionCharacters": 0
    },
    "accessMode": "PRIVATE"
}
```


### Documentation
For more information about this API, please consult out [online documentation](https://docs.sphereon.com/api/blockchain-proof/0.10/html). 