FROM jboss/base-jdk:8

ADD target/aloha-fat.jar /

EXPOSE 8080

CMD java -jar /aloha-fat.jar
