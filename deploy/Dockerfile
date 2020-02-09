FROM maven:3.6.3-jdk-8

RUN apt-get update && apt-get install git

RUN git clone https://github.com/BasdeBruyn/JDA_Alexa_Bot

WORKDIR /JDA_Alexa_Bot

RUN git checkout test_outdated_lavaplayer_error_handeling

RUN mvn -f ./pom.xml clean install -U

COPY alexa_bot.sh ./

ENTRYPOINT ["bash", "alexa_bot.sh"]

CMD [""]