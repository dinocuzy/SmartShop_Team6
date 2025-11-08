package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Utility class để kiểm tra các model có sẵn trong Gemini API
 */
public class GeminiModelChecker {
    
    /**
     * List tất cả các model có sẵn
     * @param apiKey API key
     * @return Danh sách model names
     */
    public static String[] listAvailableModels(String apiKey) {
        try {
            // Thử cả v1beta và v1
            String[] apiVersions = {
                "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey,
                "https://generativelanguage.googleapis.com/v1/models?key=" + apiKey
            };
            
            for (String urlString : apiVersions) {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        
                        // Parse JSON
                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                        
                        if (jsonResponse.has("models")) {
                            JsonArray models = jsonResponse.getAsJsonArray("models");
                            String[] modelNames = new String[models.size()];
                            for (int i = 0; i < models.size(); i++) {
                                JsonObject model = models.get(i).getAsJsonObject();
                                String name = model.get("name").getAsString();
                                // Extract model name từ "models/model-name"
                                if (name.contains("/")) {
                                    name = name.substring(name.lastIndexOf("/") + 1);
                                }
                                modelNames[i] = name;
                                
                                // Print model info (chỉ print các model hỗ trợ generateContent)
                                if (model.has("supportedGenerationMethods")) {
                                    JsonArray methods = model.getAsJsonArray("supportedGenerationMethods");
                                    boolean supportsGenerateContent = false;
                                    for (int j = 0; j < methods.size(); j++) {
                                        if (methods.get(j).getAsString().equals("generateContent")) {
                                            supportsGenerateContent = true;
                                            break;
                                        }
                                    }
                                    if (supportsGenerateContent) {
                                        System.out.println("Found model (supports generateContent): " + name);
                                    }
                                }
                            }
                            return modelNames;
                        }
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    System.err.println("Error checking API version: " + urlString);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing models: " + e.getMessage());
            e.printStackTrace();
        }
        return new String[0];
    }
}

