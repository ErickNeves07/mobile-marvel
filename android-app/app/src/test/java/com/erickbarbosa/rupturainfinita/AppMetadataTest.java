package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class AppMetadataTest {
    @Test
    public void displayNameMatchesProductIdentity() {
        assertEquals("Marvel: Ruptura Infinita", AppMetadata.DISPLAY_NAME);
    }
}
