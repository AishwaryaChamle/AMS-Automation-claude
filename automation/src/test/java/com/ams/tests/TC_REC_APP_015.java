package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_015: Verify HOB can fill onboarding form after successful interview approvals
 * Expected: HOB should be able to access and submit onboarding details for the approved lead (Raise Request)
 */
public class TC_REC_APP_015 extends BaseTest {

    @Test
    public void hobFillsOnboardingFormAndRaisesRequest() throws Exception {
        // Lead approved by HOB and Immediate Supervisor, then proceeded to onboarding
        Lead lead = onboarding.newLead();

        // HOB fills every step of the onboarding form and raises the request
        int ariTotal = onboarding.fillApplication(Config.hob(), lead, TestData.HIGH_ARI);
        String breakdown = onboardingPage.ariBreakdown();
        String message = onboardingPage.raiseRequest();
        String status = onboarding.statusFor(Config.hob(), lead);

        actual("HOB filled all onboarding steps for " + lead.fullName() + ". ARI: " + breakdown
                + ". Pop-up after RAISE REQUEST: '" + message + "'. Status: " + status);
        Assert.assertTrue(ariTotal > 0, "ARI total was not calculated");
        Assert.assertTrue(message.contains(OnboardingFlows.REQUEST_RAISED), "Request was not raised: " + message);
        Assert.assertEquals(status, OnboardingFlows.STATUS_PENDING, "Status after raising the request");
    }
}
