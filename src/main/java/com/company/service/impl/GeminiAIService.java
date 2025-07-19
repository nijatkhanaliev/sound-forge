package com.company.service.impl;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAIService {

    private final Dotenv dotenv;
    private final String API_KEY = dotenv.get("GEMINI_API_KEY");

    public String generatePlayListName(String songListPrompt) throws IOException {
        log.info("Generating playListName");
        URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonPrompt = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(songListPrompt);

        log.info("Sending request to geminiAI");
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] bytes = jsonPrompt.getBytes(UTF_8);
            outputStream.write(bytes);
        }

        log.info("getting response from geminiAI");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()))) {

            StringBuilder builder = new StringBuilder();
            String responseLine;

            while ((responseLine = reader.readLine()) != null) {
                builder.append(responseLine);
            }

            return builder.toString();
        }
    }

}
