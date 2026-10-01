package com.ams.tests;

import com.ams.base.Blocked;
import org.testng.annotations.Test;

/**
 * RLC_GV_012: Verify annual NIC data cleansing scope.
 * Expected: Annual data cleansing for existing NIC is not treated as an in-scope functional test expectation.
 */
public class RLC_GV_012 {

    @Test
    public void annualNicCleansingOutOfScope() {
        Blocked.invalid(Blocked.OUT_OF_SCOPE);
    }
}
