#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

# help
Help()
{
   # Display Help
   echo -e "This script will create a database according your credentials in application.yaml file
   echo
   echo "Syntax:   ./init-db.sh [clean]"
   echo "Options:"
   echo "clean     additionally, drop database before migrating. first time preferred"
   echo "docker    execute mysql commands through docker container"
   echo
}

while getopts ":h" option; do
   case $option in
     h)
         Help
         exit;;
     \?)
         echo "Error: Invalid option"
         exit;;
   esac
done
# @end help

# config
cd src/main/resources

export MYSQL_HOST="127.0.0.1"
export MYSQL_PORT="3306"
export DATABASE_NAME="crypto"

username=$(grep -A3 'spring:' application.yaml | tail -n1);
export MYSQL_USERNAME=${username//*username: /}

password=$(grep -A4 'spring:' application.yaml | tail -n1);
export MYSQL_PASSWORD=${password//*password: /}

datasource=$(grep -A2 'spring:' application.yaml | tail -n1);
export DATASOURCE_URL=${datasource//*url: /}

CLEAN_DB=
export MYSQL_CMD="mysql -h ${MYSQL_HOST} --port=${MYSQL_PORT} -u root"

for arg in "$@"
do
    case $arg in
      "clean")
        CLEAN_DB="true";;
      "docker")
        MYSQL_CONTAINER=$(docker-compose ps -q --filter status=running db)
        if [ -z "$MYSQL_CONTAINER" ]; then
            echo "Mysql container is not running. Use 'docker-compose start' to start containers first."
            exit
        fi
        export MYSQL_CMD="docker exec ${MYSQL_CONTAINER} ${MYSQL_CMD}";;
    esac
done

cd ${ABSOLUTE_PATH}
# @end config

printf "\n# \e[93mPreparing ${DATABASE_NAME} database\e[0m\n\n"
sleep .5

# test connections first
set -e
$MYSQL_CMD -e "SELECT now();"

set +e
$MYSQL_CMD -e "GRANT ALL PRIVILEGES ON *.* TO '$MYSQL_USERNAME'@'%';"
$MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS $DATABASE_NAME"
$MYSQL_CMD -e "SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';"
$MYSQL_CMD -e "SET GLOBAL log_bin_trust_function_creators = 1;"
set -e

if [[ $CLEAN_DB == "true" ]]; then
  echo "If database can not be dropped, please kill all connection to it:"
  ${MYSQL_CMD} -e "SELECT CONCAT('KILL ', id, ';') as 'COPY & EXECUTE IN CASE OF CONFLICT'
                   FROM INFORMATION_SCHEMA.PROCESSLIST WHERE db = 'crypto';"

  $MYSQL_CMD -e "DROP DATABASE $DATABASE_NAME"
  $MYSQL_CMD -e "CREATE DATABASE $DATABASE_NAME"
  printf "\n# \e[93mDatabase $DATABASE_NAME has been recreated.\e[0m\n"
fi
