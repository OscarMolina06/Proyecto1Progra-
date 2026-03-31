/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentacion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ClaudeAssistant {

    private static final String API_KEY = "  LLAVE DE APY PEDRILA NO LA DEJEN EN EL GIBHUB ";
    private static final String API_URL  = "https://api.anthropic.com/v1/messages";
    private static final String MODEL    = "claude-sonnet-4-20250514";
    private static final int    MAX_TOKENS = 1024;

    private static final String SYSTEM_PROMPT = """
        Eres un asistente virtual integrado en el Sistema de Padrón Electoral de Costa Rica.
        Tu rol es ayudar a los usuarios a:
        - Entender cómo usar el sistema (consultas por cédula, formatos JSON/XML).
        - Interpretar los resultados de búsqueda (provincia, cantón, distrito electoral).
        - Explicar cómo funciona el Padrón Electoral costarricense.
        - Orientar sobre los servidores TCP (puerto 5555) y HTTP (puerto 9090) del sistema.
        - Responder preguntas generales sobre el sistema electoral de Costa Rica.

        Responde siempre en español, de forma clara, concisa y profesional.
        No puedes realizar consultas directas de cédulas; para eso el usuario debe usar
        la pestaña "Consulta" de la aplicación.
        Si te preguntan algo fuera del contexto electoral/sistema, redirige amablemente al tema.
        """;

    private final HttpClient httpClient;
    private final StringBuilder conversationHistory;

    public ClaudeAssistant() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.conversationHistory = new StringBuilder();
    }

    public CompletableFuture<String> chat(String userMessage) {
        if (conversationHistory.length() > 2000) {
            conversationHistory.delete(0, conversationHistory.length() / 2);
        }
        conversationHistory.append("Usuario: ").append(userMessage).append("\n");

        String requestBody = buildRequestBody(userMessage);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type","application/json")
                .header("x-api-key",API_KEY)
                .header("anthropic-version","2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        String answer = extractText(response.body());
                        conversationHistory.append("Asistente: ").append(answer).append("\n");
                        return answer;
                    } else if (response.statusCode() == 401) {
                        return "API Key inválida. Por favor configura tu clave de Anthropic en ClaudeAssistant.java";
                    } else {
                        return "Error al contactar el asistente (HTTP " + response.statusCode() + "). Verifica tu conexion.";
                    }
                })
                .exceptionally(ex -> " Sin conexion a internet o timeout verifica tu internet e intenta de nuevo.");
    }

    public void resetConversation() {
        conversationHistory.setLength(0);
    }

    private String buildRequestBody(String userMessage) {
        String contextualMessage = conversationHistory.length() > 0
                ? "Contexto previo:\n" + conversationHistory + "\nNuevo mensaje: " + userMessage
                : userMessage;

        String safeMessage = contextualMessage
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        String safeSystem = SYSTEM_PROMPT
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        return String.format("""
                {
                  "model": "%s",
                  "max_tokens": %d,
                  "system": "%s",
                  "messages": [
                    {"role": "user", "content": "%s"}
                  ]
                }
                """, MODEL, MAX_TOKENS, safeSystem, safeMessage);
    }

    private String extractText(String json) {
        try {
            int textIdx = json.indexOf("\"text\":");
            if (textIdx == -1) return "No se pudo interpretar la respuesta del asistente.";

            int start = json.indexOf('"', textIdx + 7) + 1;
            int end   = start;
            while (end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
                end++;
            }
            return json.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        } catch (Exception e) {
            return "Error procesando la respuesta: " + e.getMessage();
        }
    }
}
