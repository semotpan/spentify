#!/bin/sh

# httpie
echo
echo "Create an account successfully"
http POST http://localhost:8080/api/accounts < account.json
