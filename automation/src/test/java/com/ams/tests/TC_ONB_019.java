package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.pages.OnboardingPage;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_019: Verify rejection by Agency Admin during onboarding approval
 * Expected: Request should be rejected and sales code should not be activated
 */
public class TC_ONB_019 extends BaseTest {

    @Test
    public void agencyAdminRejectionDoesNotActivateSalesCode() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approve(Config.lop(), lead);

        String message = onboarding.reject(Config.agencyAdmin(), lead, "Rejected by Agency Admin - automation");
        String status = onboarding.statusFor(Config.hob(), lead);
        String salesCode = onboardingPage.salesCode(lead.fullName());

        actual("Agency Admin rejected: '" + message + "'. Status for HOB: " + status + ", sales code: " + salesCode);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Status after Agency Admin rejection");
        Assert.assertFalse(salesCode.matches("\\d+"), "A sales code was generated for a rejected request: " + salesCode);
        Assert.assertNull(OnboardingPage.salesCodeIn(message), "Rejection pop-up shows a sales code");
    }
}
