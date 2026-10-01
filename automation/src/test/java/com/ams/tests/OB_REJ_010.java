package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_010: Verify repeated LOP rejection and resubmission works correctly.
 * Expected: Each LOP rejection returns the request to the original requester, and each corrected resubmission can proceed for LOP approval again.
 */
public class OB_REJ_010 extends BaseTest {

    @Test
    public void repeatedLopRejectionAndResubmission() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);

        // Two rounds of LOP rejection and HOB resubmission; each resubmission goes to LOP again
        StringBuilder rounds = new StringBuilder();
        for (int round = 1; round <= 2; round++) {
            onboarding.reject(Config.lop(), lead, "LOP rejection " + round + " - automation");
            String status = onboarding.statusFor(Config.hob(), lead);
            String resubmitted = onboarding.resubmit(Config.hob(), lead);
            boolean withLop = onboarding.isPendingWith(Config.lop(), lead);
            rounds.append("Round ").append(round).append(": status ").append(status).append(", resubmission '")
                    .append(resubmitted).append("', with LOP again: ").append(withLop).append(". ");
            Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Round " + round + ": request did not return to HOB");
            Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "Round " + round + ": resubmission failed");
            Assert.assertTrue(withLop, "Round " + round + ": resubmitted request is not with LOP");
        }
        actual(rounds.toString());
    }
}
