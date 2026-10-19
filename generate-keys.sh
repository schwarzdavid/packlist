#!/usr/bin/env bash

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out ./user-service/src/main/resources/keys/private.pem
openssl pkey -in ./user-service/src/main/resources/keys/private.pem -pubout -out ./user-service/src/main/resources/keys/public.pem