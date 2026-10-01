package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_018: Verify rejection by LOP during onboarding approval
 * Expected: Request should be rejected and no further processing should happen
 */
public class TC_ONB_018 extends BaseTest {

    @Test
    public void lopRejectionStopsOnboardingRequest() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);

        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");
        boolean inAgencyAdminTray = onboarding.isPendingWith(Config.agencyAdmin(), lead);
        String status = onboarding.statusFor(Config.hob(), lead);

        actual("LOP rejected. Status for HOB: " + status + ". In Agency Admin tray: " + inAgencyAdminTray);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Status after LOP rejection");
        Assert.assertFalse(inAgencyAdminTray, "Rejected request went to Agency Admin");
    }
}
