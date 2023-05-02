import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class sendJson {

    public static void main(String[] args) throws Exception {
//        String jsonFilePath = "src/main/java/barbers.json";
//        String urlString = "http://localhost:8081/registerBarber"; // Replace with the URL you want to send the requests to
//
//        BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
//        StringBuilder jsonBuilder = new StringBuilder();
//        String line = reader.readLine();
//        while (line != null) {
//            jsonBuilder.append(line);
//            line = reader.readLine();
//        }
//        reader.close();
//
//        JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            System.out.println(jsonObject.toString());
//            sendRequest(urlString, jsonObject.toString());
//        }
        getNearMe();
    }

    public static void sendRequest(String urlString, String json) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(json);
        writer.flush();
        writer.close();
        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode);
    }

    public static void getNearMe() throws IOException {
        String clientZip = "07621";
        String urlString = "http://localhost:8081/getBarbersNearMe/07621";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode);

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = inputReader.readLine()) != null) {
            response.append(inputLine);
        }
        inputReader.close();

        System.out.println("Response body: " + response.toString());
    }
}

