package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_006: Verify the original requester can resubmit the request multiple times after repeated SMBD rejections.
 * Expected: Every rejection returns the request to the original requester, who can correct and resubmit it again.
 */
public class OB_REJ_006 extends BaseTest {

    @Test
    public void requesterResubmitsAfterRepeatedSrMbdRejections() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        // Two rounds of Sr MBD rejection and HOB resubmission
        StringBuilder rounds = new StringBuilder();
        for (int round = 1; round <= 2; round++) {
            onboarding.reject(OnboardingFlows.smbd(), lead, "Sr MBD rejection " + round + " - automation");
            String status = onboarding.statusFor(Config.hob(), lead);
            String resubmitted = onboarding.resubmit(Config.hob(), lead);
            rounds.append("Round ").append(round).append(": status ").append(status).append(", resubmission '").append(resubmitted).append("'. ");
            Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Round " + round + ": request did not return to HOB");
            Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "Round " + round + ": resubmission failed: " + resubmitted);
        }
        boolean backWithSrMbd = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);

        actual(rounds + "In Sr MBD tray after the last resubmission: " + backWithSrMbd);
        Assert.assertTrue(backWithSrMbd, "Last resubmission is not with the Sr MBD");
    }
}
