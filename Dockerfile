FROM jboss/base-jdk:8

ADD target/aloha.jar .

EXPOSE 8080

CMD java -jar aloha.jar
