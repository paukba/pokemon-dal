package com.example.pokemon.app;

import com.google.gson.Gson;
import com.example.pokemon.model.Trade;
import com.example.pokemon.service.ServiceResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Small synchronous REST client used by the ConsoleApp to call the local REST microservice.
 * Base URL usually: http://localhost:4567
 */
public class RestClient {
    private final String baseUrl;
    private final Gson gson = new Gson();

    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Trade proposeTrade(Trade t) throws Exception {
        String url = baseUrl + "/trades/propose";
        String body = gson.toJson(t);
        String resp = post(url, body);
        return gson.fromJson(resp, Trade.class);
    }

    public ServiceResult acceptTrade(int tradeId) throws Exception {
        String url = baseUrl + "/trades/" + tradeId + "/accept";
        // POST with empty body
        String resp = post(url, "");
        return gson.fromJson(resp, ServiceResult.class);
    }

    public double getOwnerValue(int ownerId) throws Exception {
        String url = baseUrl + "/owners/" + ownerId + "/value";
        String resp = get(url);
        return gson.fromJson(resp, Double.class);
    }

    private String get(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(15000);
        int status = con.getResponseCode();
        InputStream in;
        if (status >= 200 && status < 300) in = con.getInputStream();
        else in = con.getErrorStream();
        try (InputStream is = in; BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            if (status >= 200 && status < 300) return sb.toString();
            else throw new RuntimeException("HTTP " + status + ": " + sb.toString());
        }
    }

    private String post(String urlStr, String body) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setConnectTimeout(5000);
        con.setReadTimeout(15000);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        if (body != null && !body.isEmpty()) {
            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            // ensure we send zero-length body so some servers accept POST
            con.setFixedLengthStreamingMode(0);
        }

        int status = con.getResponseCode();
        InputStream in;
        if (status >= 200 && status < 300) in = con.getInputStream();
        else in = con.getErrorStream();

        try (InputStream is = in; BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            if (status >= 200 && status < 300) return sb.toString();
            else throw new RuntimeException("HTTP " + status + ": " + sb.toString());
        }
    }
}