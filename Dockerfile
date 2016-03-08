FROM rhel7

RUN yum -y install java && yum clean all

EXPOSE 8080

CMD java -jar aloha.jar

ADD target/aloha.jar .
