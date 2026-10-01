package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_005: Verify an onboarding request with ARI < 30 does not move to LOP approval when SMBD rejects it.
 * Expected: Request remains in the rejected/returned state and is routed back to the original requester instead of LOP.
 */
public class OB_REJ_005 extends BaseTest {

    @Test
    public void srMbdRejectionDoesNotMoveRequestToLop() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);

        onboarding.reject(OnboardingFlows.smbd(), lead, "Rejected by Sr MBD - automation");
        boolean inLopTray = onboarding.isPendingWith(Config.lop(), lead);
        String status = onboarding.statusFor(Config.hob(), lead);

        actual("After Sr MBD rejection - in LOP tray: " + inLopTray + ", status for HOB: " + status);
        Assert.assertFalse(inLopTray, "Rejected request moved to LOP");
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Request did not return to the requester");
    }
}
