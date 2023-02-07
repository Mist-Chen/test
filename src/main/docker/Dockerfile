FROM openjdk:8
ENV APPLICATION='teldrassil-sprint'
VOLUME /tmp

ADD sprint-4.1.0.jar sprint.jar

RUN bash -c 'touch /sprint.jar'
RUN ln -sf /usr/share/zoneinfo/America/New_York /etc/localtime
RUN echo 'America/New_York' >/etc/timezone

EXPOSE 9554

ENTRYPOINT ["java", "-jar", "/sprint.jar"]
