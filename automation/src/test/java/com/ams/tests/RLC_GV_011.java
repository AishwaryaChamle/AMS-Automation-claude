package com.ams.tests;

import com.ams.base.Blocked;
import org.testng.annotations.Test;

/**
 * RLC_GV_011: Verify multi-language document scope.
 * Expected: Multi-language document rendering/download is not treated as an expected functional requirement.
 */
public class RLC_GV_011 {

    @Test
    public void multiLanguageDocumentsOutOfScope() {
        Blocked.invalid(Blocked.OUT_OF_SCOPE);
    }
}
