## Stock App

This is an api for serving stockPriceData data via an oauth2 authenticated endpoint.

## Goals

The goal of this application is to provide spring boot based api for serving ford stockPriceData data.

## Installation
Make sure you have maven (https://maven.apache.org/download.cgi)

Make sure maven runs with jdk 1.8 x64 (http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html)

Navigate to fordstocks/api and run "mvn spring-boot:run" to start the api

Navigate to fordstocks/authentication and run "mvn spring-boot:run" to start the authentication server

- Api runs at http://localhost:8989 by default

- Authentication server runs at http://localhost:9999 by default (http://localhost:9999/authentication/oauth/token for token)

## Usage

To authenticate to the api you need to retrieve a client credentials bearer token from the authentication server

- Client-id: ford 

- Client-secret: fordsecret

The fordstocks/api/src/test/resources folder contains:

 - a shell script to get a bearer token from the authentication service
 
 - several sample request urls to the api
 
 - a similar csv file called honda.csv ---> Move this file to the fordstocks/api/src/main/resources/stocks folder before starting up the application to also load honda stocks
 (The file is not added initially since we don't want to scare anyone in the ford shareholders meeting)

## Trouble Shooting
- If for some reason the embedded mongoDB fails to start, make sure the application (via IDE/mvn) runs with a 64x version of java)
