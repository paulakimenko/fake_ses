FROM maven:3.9-eclipse-temurin-8 AS builder

WORKDIR /build

# Cache dependencies first
COPY pom.xml ./
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy sources and resources, then build fat jar
COPY src ./src
COPY resources ./resources
RUN mvn -q -e -DskipTests=false package

FROM eclipse-temurin:17-jre

ENV PORT=8567
ENV THREAD_COUNT=8
ENV WORK_DIR=/home/appuser/app/messages

COPY --from=builder /build/target/fake-ses-*-jar-with-dependencies.jar /fake-ses.jar

# Static assets served from external location
COPY --from=builder /build/resources/public /resources/public

# Entrypoint
COPY docker/entry_point.sh /entry_point.sh
RUN chmod +x /entry_point.sh \
	&& adduser --disabled-password --gecos "" appuser \
	&& mkdir -p /home/appuser/app \
	&& chown -R appuser:appuser /resources /entry_point.sh /fake-ses.jar /home/appuser/app

WORKDIR /home/appuser/app
USER appuser

EXPOSE $PORT

ENTRYPOINT ["/entry_point.sh"]

