# Slack Notifier

## Introduction
The Slack Notifier is a Java-based microservice that sends notifications to a configured Slack channel upon receiving HTTP POST requests. It's designed to operate within a Kubernetes environment, reacting to the events related to custom resource changes.

## Tech Stack
- Language: Java 17
- Web Framework: SparkJava
- Build Tool: Maven
- Notifications: Slack API

## How to Run
Ensure you have Java 17, Maven, and a working Slack workspace with a token configured.

1. Clone the repository:
```
git clone https://github.com/Grizvold/SlackNotifier.git
```

2. Change into the project directory:
```
cd SlackNotifier
```

3. Build the project:
```
mvn clean install
```

4. Set your environment variables:
```
export SLACK_TOKEN=your_slack_token
export PORT=your_desired_port (optional, defaults to 4567 if not specified)
```

5. Run the service:
```
java -jar target/SlackNotifier-1.0.jar
```


## API Endpoint
The following endpoint is available:

- **Slack Notification**: Send a POST request to `http://localhost:{PORT}/` with the JSON payload describing the event in the request body.

## Payload Structure
The service expects a JSON payload with the following structure:
```json
{
 "apiVersion": "api_version",
 "kind": "kind",
 "operation": "operation"
}
```

where:

`apiVersion` is the API version of the resource that triggered the event.

`kind` is the kind of the resource that triggered the event.

`operation` is the operation that was performed on the resource (e.g., CREATE, UPDATE, DELETE).