#!/bin/bash

APP_VERSION_FILE=./app.version

echo "Removing built project artifacts..."
/bin/rm -rf ./build

echo "Removing previous outputs"
/bin/rm output.csv
/bin/rm logs/runner.log
touch logs/runner.log

echo "Done."
echo "Maven build clean..."
mvn clean

[ -e ${APP_VERSION_FILE} ] && echo "Deleting ${APP_VERSION_FILE} ..." && /bin/rm -f ${APP_VERSION_FILE} && echo "Deleted."

echo "Done."
