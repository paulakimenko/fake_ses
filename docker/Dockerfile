FROM openjdk:8

ENV PORT 8000
ENV THREAD_COUNT 8
ENV WORK_DIR ./messages

ADD /sesmock-*-jar-with-dependencies.jar /sesmock.jar

COPY /entry_point.sh //
RUN chmod +x /entry_point.sh

EXPOSE $PORT

ENTRYPOINT ["/entry_point.sh"]

