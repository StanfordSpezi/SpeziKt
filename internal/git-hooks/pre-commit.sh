#!/bin/bash

echo "Running detekt..."
./gradlew detekt

detektStatus=$?
if [[ "$detektStatus" = 0 ]] ; then
    echo "Detekt run successfully"
    exit 0
else
    echo 1>&2 "Detekt found violations"
    exit 1
fi