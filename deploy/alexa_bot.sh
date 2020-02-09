#!/bin/bash
mvn -f ./pom.xml install -U
java -jar ./target/AlexaBot-1.0-jar-with-dependencies.jar $1
