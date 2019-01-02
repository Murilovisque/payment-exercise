#!/bin/bash

# Maven settings
export mvn='mvn -Dmaven.repo.local=/maven-data/.m2/repository'
echo "alias mvn='$mvn'" >> /root/.bashrc

#export deploy='mvn install -f /payment-exercise/payment-api; mvn package -f /payment-exercise/payment-checkout; ps ax | grep java | grep -Poh "^\s*\d+" | head -n 1 | xargs -x kill; nohup java -Dlogback.configurationFile=/payment-checkout/src/main/resouces/logback.xml -jar /payment-exercise/payment-checkout/target/payment-checkout-1.0-SNAPSHOT-jar-with-dependencies.jar &'
#echo "alias deploy='$deploy'" >> /root/.bashrc

#$mvn install -f /payment-exercise/payment-api
$mvn package -f /payment-exercise/payment-checkout

java -Dlogback.configurationFile=/payment-exercise/payment-checkout/src/main/resouces/logback.xml -Ddatabase.config.path=/payment-exercise/payment-checkout/src/main/resouces/database.properties -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8457,server=y,suspend=n -jar /payment-exercise/payment-checkout/target/payment-checkout-1.0-SNAPSHOT-jar-with-dependencies.jar
exit $?