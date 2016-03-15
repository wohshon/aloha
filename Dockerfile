FROM jboss/base-jdk:8

EXPOSE 8080

ADD target/aloha.jar .

CMD java -jar aloha.jar
