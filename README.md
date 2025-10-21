# Fake SES

Inspired by [Fake DynamoDB][fake_dynamo] and [Fake SQS][fake_sqs]. A tiny HTTP service that emulates a subset of AWS SES (SendEmail and SendRawEmail) so you can test email flows locally.

## Quick start

Run with Docker (recommended):

```
docker run -it -p 8567:8567 --name fake_ses paulakimenko/fake_ses:latest
```

Environment variables:

- PORT: HTTP port (default 8567)
- THREAD_COUNT: Jetty worker threads (default 8)
- WORK_DIR: Directory to store messages (default /home/appuser/app/messages in the image)

Examples:

- List messages
```
GET /api/messages
```

- Filter messages
```
GET /api/messages?filter=subject:Hello;received_after:1480349900
```
Supported filters: subject, received_before, received_after, source, destination, text_contains, html_contains

- Delete all messages
```
DELETE /api/messages
```

- Send a message (SES Query API style)
```
POST /
Action=SendEmail&
Message.Subject.Data=Hello&
Source=sender@example.com&
Destination.ToAddresses.member.1=to@example.com&
Message.Body.Text.Data=Hello
```

Tip: set the AWS client endpoint (or SES base URL) to this service to test your integration.

## Build from source

This repository ships a multi-stage Dockerfile that builds and runs the service without requiring local Java/Maven:

```
docker build -f docker/Dockerfile -t paulakimenko/fake_ses:0.3 -t paulakimenko/fake_ses:latest .
```

Run tests inside the build (enabled by default). To skip tests (not recommended):

```
docker build -f docker/Dockerfile --build-arg MAVEN_OPTS="-DskipTests" -t paulakimenko/fake_ses:dev .
```

## Development

- Java 8 compatible runtime; builds and tests run against JDK 8 in Docker for stability
- Frameworks: SparkJava, Gson, Jetty
- Data storage: simple JSON files in WORK_DIR

### Local run (without Docker)

1) Build a fat jar:

```
mvn package
```

2) Run:

```
java \
  -Dfakeses.port=8567 \
  -Dfakeses.threadcount=8 \
  -Dfakeses.workdir=./messages \
  -jar target/fake-ses-*-jar-with-dependencies.jar
```

## Compatibility note

This mock implements the SES Query API (v2010-12-01) for SendEmail and SendRawEmail. It accepts the same parameter names used by AWS SDKs (e.g., Message.Subject.Data, Message.Body.Text.Data, Destination.ToAddresses.member.N) and returns an SES-style XML response with MessageId. For most SDKs, pointing the SES endpoint to this service is sufficient for local testing.

## Changelog

- 0.3
  - Updated base image and dependencies (Spark 2.9.x, Gson 2.10.x, SLF4J 1.7.36)
  - Multi-stage Docker build with tests
  - Minor fixes: consistent message IDs, safer filters, multipart parsing

  [fake_dynamo]: https://github.com/ananthakumaran/fake_dynamo
  [fake_sqs]: https://github.com/iain/fake_sqs
