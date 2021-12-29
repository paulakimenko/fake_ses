# Fake SES

Inspired by [Fake DynamoDB][fake_dynamo] and [Fake SQS][fake_sqs]

## Running

```
$ docker run -it -p 8567:8567 paulakimenko/fake_ses
```

## Usage

Set endpoint in your AWS client config to this service.

You can access the dashboard at `http://localhost:8567/dashboard`.

There are also API endpoints:

- to see all messages 
```
GET /api/messages
```
- to filter messages
```
GET /api/messages?filter=subject:somesubject;received_after:1480349900
```
You can use these filters : [subject, received_before, received_after, source, destination, text_contains, html_contains]
- to remove all received messages
```
DELETE /api/messages
```
 
## Development

Requires Java 8.

Run `mvn clean package docker:build` to build the docker image.

  [fake_dynamo]: https://github.com/ananthakumaran/fake_dynamo
  [fake_sqs]: https://github.com/iain/fake_sqs
