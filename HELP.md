# Getting Started

### Sphereon Store Account
To run this sample you will first need to register yourself in our API store. The following steps are required to get this example to work:
* Go to the [Sphereon API store](https://store.sphereon.com/store/) and click "Sign-Up"
* Once you have registered and signed in, go to menu "Applications" and either create a new entry or open the default one.
* Go to tab "Sandbox Keys" and create a Consumer Key and Secret. Make sure the "Client Credential" box s ticked.
* Copy the values in fields "Consumer Key" and "Consumer Secret" and save them for later.
* Go to menu "APIS" and select API "Blockchain-Proof-0.10" -> [DIRECT LINK](https://store.sphereon.com/store/apis/info?name=Blockchain-Proof&version=0.10&provider=admin)
* Under drop down list "Applications", choose the application name for which you just collected the consumer key & secret from, then click on "Subscribe"
* Next the client id & client secret properties have to populated with the consumer key & secret values from the store. There are three locations this can be done:  
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

### Upload method
The example implements two upload methods, STREAM & CONTENT which can be configured in application.properties. 
(You can copy the application.properties file from the project's resources directory to config/application.properties relative of your working directory)
```
sphereon.blockchain-proof-api.upload-method=STREAM
```
In this mode the file to register or verify is uploaded our cloud and temporary stored and hashed. 
A powerful extra feature you get in this mode is the support for hashing document content, meaning that if for instance someone opens then 
saves a PDF file, the new file will still validate as long as the content didn't change.

```
sphereon.blockchain-proof-api.upload-method=CONTENT
```
In this mode the file to register or verify is hashed locally. No content is actually sent over the network, only the hash.
This is the most secure option for documents containing private/confidential information. Content hashing is not available in this mode as
we don't get the content.


 

