#!/usr/bin/env bash

if [ "${APP_ENV}" == "dev" ]; then
    echo "dev-done";
else
    echo "nonDev-done";
fi
