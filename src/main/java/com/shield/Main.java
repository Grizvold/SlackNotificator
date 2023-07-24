package com.shield;

import com.google.gson.Gson;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.PlainTextObject;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.Optional;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Get port from environment variable, default to 8080 if not present
        String portEnv = System.getenv("PORT");
        int portNumber = (portEnv == null) ? 8080 : Integer.parseInt(portEnv);

        port(portNumber); // set the port SparkJava will use

        post("/", (req, res) -> handleSlackNotification(req, res));
    }

    private static String handleSlackNotification(Request request, Response response) {
        Gson gson = new Gson();
        SlackPayload payload = gson.fromJson(request.body(), SlackPayload.class);

        String slackMessage = String.format("API Version: %s\nKind: %s\nOperation: %s",
                payload.apiVersion,
                payload.kind,
                payload.operation);

        Slack slack = Slack.getInstance();
        String token = Optional.ofNullable(System.getenv("SLACK_TOKEN"))
                .orElseThrow(() -> new IllegalStateException("Environment variable `SLACK_TOKEN` is not set"));

        String channel = Optional.ofNullable(System.getenv("SLACK_CHANNEL"))
                .orElse("#general"); // use "#general" as default channel

        MethodsClient methods = slack.methods(token);
        ChatPostMessageRequest postMessageRequest = ChatPostMessageRequest.builder()
                .channel(channel)
                .blocks(Arrays.asList(SectionBlock.builder().text(PlainTextObject.builder().text(slackMessage).build()).build()))
                .build();

        try {
            ChatPostMessageResponse chatResponse = methods.chatPostMessage(postMessageRequest);
            if (chatResponse.isOk()) {
                return "Success";
            } else {
                response.status(500);
                return "Failed to send Slack message: " + chatResponse.getError();
            }
        } catch (Exception e) {
            response.status(500);
            return "Failed to send Slack message: " + e.getMessage();
        }
    }

    private class SlackPayload {
        public String apiVersion;
        public String kind;
        public String operation;
    }
}
