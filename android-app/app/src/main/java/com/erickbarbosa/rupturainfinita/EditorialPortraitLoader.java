package com.erickbarbosa.rupturainfinita;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Loads one attributed Comic Vine portrait per game character, reused by all variants. */
final class EditorialPortraitLoader {
    private static final int MAX_IMAGE_BYTES = 3_000_000;
    private static final long METADATA_MAX_AGE_MS = 24L * 60 * 60 * 1000;
    private final BackendClient backend;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Handler main = new Handler(Looper.getMainLooper());
    private final Object lock = new Object();
    private final LruCache<String, Portrait> cache = new LruCache<String, Portrait>(12 * 1024 * 1024) {
        @Override protected int sizeOf(String key, Portrait value) {
            return value.bitmap.getByteCount();
        }
    };
    private final Map<String, List<Target>> pending = new HashMap<>();
    private final Set<String> unavailable = new HashSet<>();

    EditorialPortraitLoader(BackendClient backend) {
        this.backend = backend;
    }

    void load(String gameId, String characterName, ImageView image, TextView attribution) {
        image.setTag(gameId);
        image.setImageDrawable(null);
        if (attribution != null) {
            attribution.setText(R.string.editorial_portrait_loading);
            attribution.setClickable(false);
            attribution.setFocusable(false);
            attribution.setOnClickListener(null);
        }
        Portrait cached;
        synchronized (lock) {
            cached = cache.get(gameId);
            if (cached == null && unavailable.contains(gameId)) {
                showUnavailable(attribution);
                return;
            }
            if (cached == null) {
                List<Target> waiting = pending.get(gameId);
                if (waiting != null) {
                    waiting.add(new Target(characterName, image, attribution));
                    return;
                }
                waiting = new ArrayList<>();
                waiting.add(new Target(characterName, image, attribution));
                pending.put(gameId, waiting);
            }
        }
        if (cached != null) {
            apply(gameId, new Target(characterName, image, attribution), cached);
        } else {
            executor.execute(() -> fetch(gameId));
        }
    }

    private void fetch(String gameId) {
        Portrait portrait = null;
        try {
            JSONObject metadata = backend.cachedGetFresh(
                    "/v1/editorial/game-characters/" + gameId, METADATA_MAX_AGE_MS);
            if (!gameId.equals(metadata.getString("game_id"))) {
                throw new IllegalStateException("Portrait game ID mismatch");
            }
            String imageUrl = metadata.optString("image_url", "");
            String siteUrl = metadata.getString("site_url");
            validateComicVineUrl(siteUrl);
            if (!imageUrl.isEmpty()) {
                portrait = new Portrait(downloadImage(imageUrl), siteUrl);
            }
        } catch (Exception ignored) {
            // Missing backend or image leaves the offline game usable.
        }
        Portrait result = portrait;
        main.post(() -> {
            List<Target> waiting;
            synchronized (lock) {
                waiting = pending.remove(gameId);
                if (result == null) unavailable.add(gameId);
                else cache.put(gameId, result);
            }
            if (waiting == null) return;
            for (Target target : waiting) {
                if (result == null) showUnavailable(target.attribution);
                else apply(gameId, target, result);
            }
        });
    }

    private static Bitmap downloadImage(String address) throws Exception {
        URL url = validateComicVineUrl(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(8_000);
        connection.setReadTimeout(12_000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Accept", "image/jpeg,image/png,image/webp");
        try {
            String contentType = connection.getContentType();
            if (connection.getResponseCode() != 200
                    || contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalStateException("Editorial image is unavailable");
            }
            byte[] bytes;
            try (InputStream input = connection.getInputStream();
                 ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    if (output.size() + read > MAX_IMAGE_BYTES) {
                        throw new IllegalStateException("Editorial image exceeds size limit");
                    }
                    output.write(buffer, 0, read);
                }
                bytes = output.toByteArray();
            }
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bounds);
            if (bounds.outWidth < 1 || bounds.outHeight < 1
                    || bounds.outWidth > 8000 || bounds.outHeight > 8000) {
                throw new IllegalStateException("Editorial image dimensions are invalid");
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            while (bounds.outWidth / options.inSampleSize > 720
                    || bounds.outHeight / options.inSampleSize > 720) {
                options.inSampleSize *= 2;
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            if (bitmap == null) throw new IllegalStateException("Editorial image could not be decoded");
            return bitmap;
        } finally {
            connection.disconnect();
        }
    }

    private static URL validateComicVineUrl(String value) throws Exception {
        URL url = new URL(value);
        if (!"https".equals(url.getProtocol())
                || !"comicvine.gamespot.com".equalsIgnoreCase(url.getHost())
                || url.getUserInfo() != null) {
            throw new IllegalArgumentException("Editorial URL is not a Comic Vine HTTPS URL");
        }
        return url;
    }

    private void apply(String gameId, Target target, Portrait portrait) {
        if (!gameId.equals(target.image.getTag())) return;
        target.image.setImageBitmap(portrait.bitmap);
        target.image.setContentDescription("Retrato editorial de " + target.characterName
                + ", fonte Comic Vine; a imagem pode não representar esta variante");
        if (target.attribution != null) {
            target.attribution.setText(R.string.editorial_portrait_credit);
            target.attribution.setClickable(true);
            target.attribution.setFocusable(true);
            target.attribution.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(portrait.siteUrl));
                try { view.getContext().startActivity(intent); }
                catch (android.content.ActivityNotFoundException ignored) { }
            });
        }
    }

    private void showUnavailable(TextView attribution) {
        if (attribution != null) attribution.setText(R.string.editorial_portrait_unavailable);
    }

    void close() {
        executor.shutdownNow();
    }

    private static final class Portrait {
        final Bitmap bitmap;
        final String siteUrl;

        Portrait(Bitmap bitmap, String siteUrl) {
            this.bitmap = bitmap;
            this.siteUrl = siteUrl;
        }
    }

    private static final class Target {
        final String characterName;
        final ImageView image;
        final TextView attribution;

        Target(String characterName, ImageView image, TextView attribution) {
            this.characterName = characterName;
            this.image = image;
            this.attribution = attribution;
        }
    }
}
