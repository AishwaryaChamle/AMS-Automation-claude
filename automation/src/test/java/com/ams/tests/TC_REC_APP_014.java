package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_014: Verify LOP can fill onboarding form after successful interview approvals
 * Expected: LOP should be able to access and submit onboarding details for the approved lead (Raise Request) but First AS or HOB should proceed the lead.
 */
public class TC_REC_APP_014 extends BaseTest {

    @Test
    public void lopFillsOnboardingFormAfterHobProceeds() throws Exception {
        // Lead approved by HOB and Immediate Supervisor, then proceeded to onboarding by HOB
        Lead lead = onboarding.newLead();

        // LOP fills every step of the onboarding form and raises the request
        int ariTotal = onboarding.fillApplication(Config.lop(), lead, TestData.HIGH_ARI);
        String breakdown = onboardingPage.ariBreakdown();
        String message = onboardingPage.raiseRequest();
        String status = onboarding.statusFor(Config.lop(), lead);

        actual("LOP filled all onboarding steps for " + lead.fullName() + ". ARI: " + breakdown
                + ". Pop-up after RAISE REQUEST: '" + message + "'. Status: " + status);
        Assert.assertTrue(ariTotal > 0, "ARI total was not calculated");
        Assert.assertTrue(message.contains(OnboardingFlows.REQUEST_RAISED), "Request was not raised: " + message);
        Assert.assertEquals(status, OnboardingFlows.STATUS_PENDING, "Status after raising the request");
    }
}
