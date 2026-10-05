package com.erickbarbosa.rupturainfinita;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Small bounded HTTP client; provider keys stay on the server. */
final class BackendClient {
    private static final String PREFS = "backend_cache";
    private static final int MAX_BODY = 1_000_000;
    private final Context context;
    private final String baseUrl;

    static final class HttpStatusException extends Exception {
        final int statusCode;
        HttpStatusException(int statusCode) {
            super("Backend returned HTTP " + statusCode);
            this.statusCode = statusCode;
        }
    }

    static boolean shouldRetryDeadpoolWithoutContext(int statusCode, JSONObject request) {
        return statusCode == 422 && request != null && request.has("game_context");
    }

    BackendClient(Context context, String baseUrl) {
        this.context = context.getApplicationContext();
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim().replaceAll("/+$", "");
        if (!this.baseUrl.isEmpty() && !this.baseUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Backend base URL must use HTTPS");
        }
    }

    JSONObject get(String path) throws Exception {
        if (baseUrl.isEmpty()) throw new IllegalStateException("Backend URL is not configured");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(baseUrl + path).openConnection();
            connection.setRequestMethod("GET"); connection.setConnectTimeout(15_000); connection.setReadTimeout(45_000);
            connection.setRequestProperty("Accept", "application/json");
            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
            if (stream == null) throw new IllegalStateException("Backend returned no response");
            byte[] bytes;
            try (InputStream input = stream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192]; int read;
                while ((read = input.read(buffer)) != -1) {
                    if (output.size() + read > MAX_BODY) throw new IllegalStateException("Backend response is too large");
                    output.write(buffer, 0, read);
                }
                bytes = output.toByteArray();
            }
            JSONObject result = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
            if (code < 200 || code >= 300) throw new IllegalStateException("Backend request failed: " + code);
            return result;
        } finally { if (connection != null) connection.disconnect(); }
    }

    JSONObject cachedGet(String path) throws Exception {
        SharedPreferences cache = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        try {
            JSONObject remote = get(path);
            cache.edit().putString(path, remote.toString()).apply();
            return remote;
        } catch (Exception unavailable) {
            String saved = cache.getString(path, null);
            if (saved == null) throw unavailable;
            return new JSONObject(saved);
        }
    }

    JSONObject cachedGetFresh(String path, long maxAgeMillis) throws Exception {
        SharedPreferences cache = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String saved = cache.getString(path, null);
        long savedAt = cache.getLong(path + ":saved_at", 0);
        long age = System.currentTimeMillis() - savedAt;
        if (saved != null && savedAt > 0 && age >= 0 && age < maxAgeMillis) {
            return new JSONObject(saved);
        }
        try {
            JSONObject remote = get(path);
            cache.edit().putString(path, remote.toString())
                    .putLong(path + ":saved_at", System.currentTimeMillis()).apply();
            return remote;
        } catch (Exception unavailable) {
            if (saved == null) throw unavailable;
            return new JSONObject(saved);
        }
    }

    JSONObject post(String path, JSONObject payload) throws Exception {
        if (baseUrl.isEmpty()) throw new IllegalStateException("Backend URL is not configured");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(baseUrl + path).openConnection();
            connection.setRequestMethod("POST"); connection.setConnectTimeout(15_000); connection.setReadTimeout(45_000);
            connection.setDoOutput(true); connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream output = connection.getOutputStream()) { output.write(body); }
            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
            if (stream == null) throw new IllegalStateException("Backend returned no response");
            try (InputStream input = stream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096]; int read;
                while ((read = input.read(buffer)) != -1) {
                    if (output.size() + read > 100_000) throw new IllegalStateException("Backend response is too large");
                    output.write(buffer, 0, read);
                }
                if (code < 200 || code >= 300) throw new HttpStatusException(code);
                return new JSONObject(new String(output.toByteArray(), StandardCharsets.UTF_8));
            }
        } finally { if (connection != null) connection.disconnect(); }
    }

    static String encode(String value) throws Exception { return URLEncoder.encode(value, "UTF-8"); }
}
