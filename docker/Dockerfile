FROM openjdk:8

ENV PORT 8567
ENV THREAD_COUNT 8
ENV WORK_DIR ./messages

ADD /fake-ses-*-jar-with-dependencies.jar /fake-ses.jar

COPY /public /resources/public
COPY /entry_point.sh //
RUN chmod +x /entry_point.sh

EXPOSE $PORT

ENTRYPOINT ["/entry_point.sh"]

