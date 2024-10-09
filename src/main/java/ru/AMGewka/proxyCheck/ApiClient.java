package ru.AMGewka.proxyCheck;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ApiClient {
    private static final String API_URL = "http://proxycheck.io/v2/";

    public static boolean checkProxy(String ip, String apiKey) {
        try {
            URL url = new URL(API_URL + ip + "?key=" + apiKey + "&vpn=1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString().contains("\"proxy\":true");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> loadList(File file) {
        if (!file.exists()) {
            return new ArrayList<>(); // Возвращаем пустой список, если файл не существует
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> list = new Gson().fromJson(br, new TypeToken<List<String>>(){}.getType());
            return list != null ? list : new ArrayList<>(); // Возвращаем пустой список, если list == null
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }

    public static void saveList(File file, HashSet<String> list) {
        try (Writer writer = new FileWriter(file)) {
            new Gson().toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}