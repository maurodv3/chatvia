#!/bin/bash

java -jar -Dspring.profiles.active=client ../build/libs/app.jar  "$@"