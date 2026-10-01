package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_017: Verify rejection by Sr MBD for ARI < 30 case
 * Expected: Request should be rejected and should not proceed to LOP or Agency Admin
 */
public class TC_ONB_017 extends BaseTest {

    @Test
    public void srMbdRejectionStopsRequestForAriBelow30() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);

        onboarding.reject(OnboardingFlows.smbd(), lead, "Rejected by Sr MBD - automation");
        boolean inLopTray = onboarding.isPendingWith(Config.lop(), lead);
        boolean inAgencyAdminTray = onboarding.isPendingWith(Config.agencyAdmin(), lead);
        String status = onboarding.statusFor(Config.hob(), lead);

        actual("Sr MBD rejected. Status for HOB: " + status + ". In LOP tray: " + inLopTray + ", in Agency Admin tray: " + inAgencyAdminTray);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Status after Sr MBD rejection");
        Assert.assertFalse(inLopTray, "Rejected request went to LOP");
        Assert.assertFalse(inAgencyAdminTray, "Rejected request went to Agency Admin");
    }
}
