#!/usr/bin/env bash
curl --request POST \
  --url http://localhost:9999/authentication/oauth/token \
  --header 'Authorization: Basic Zm9yZDpmb3Jkc2VjcmV0' \
  --header 'Cache-Control: no-cache' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data grant_type=client_credentials