package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

public final class BackendClientTest {
    @Test public void legacyDeadpoolRetryOnlyAppliesTo422WithGameContext() throws Exception {
        JSONObject currentRequest = new JSONObject()
                .put("context_id", "app")
                .put("prompt", "Comente minha colecao")
                .put("game_context", "Personagens: Homem-Aranha");

        assertTrue(BackendClient.shouldRetryDeadpoolWithoutContext(422, currentRequest));
        assertFalse(BackendClient.shouldRetryDeadpoolWithoutContext(503, currentRequest));
        assertFalse(BackendClient.shouldRetryDeadpoolWithoutContext(422,
                new JSONObject().put("context_id", "app")));
    }
}
