package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_014: Verify repeated Agency Admin rejection and resubmission works correctly.
 * Expected: Each rejection returns the request to the original requester and each resubmission follows the required approval sequence again.
 */
public class OB_REJ_014 extends BaseTest {

    @Test
    public void repeatedAgencyAdminRejectionAndResubmission() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);

        // Two rounds: LOP approves, Agency Admin rejects, HOB resubmits
        StringBuilder rounds = new StringBuilder();
        for (int round = 1; round <= 2; round++) {
            onboarding.approve(Config.lop(), lead);
            onboarding.reject(Config.agencyAdmin(), lead, "Agency Admin rejection " + round + " - automation");
            String status = onboarding.statusFor(Config.hob(), lead);
            String resubmitted = onboarding.resubmit(Config.hob(), lead);
            boolean withLop = onboarding.isPendingWith(Config.lop(), lead);
            rounds.append("Round ").append(round).append(": status ").append(status).append(", resubmission '")
                    .append(resubmitted).append("', with LOP again: ").append(withLop).append(". ");
            Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Round " + round + ": request did not return to HOB");
            Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "Round " + round + ": resubmission failed");
            Assert.assertTrue(withLop, "Round " + round + ": resubmitted request did not start again with LOP");
        }
        actual(rounds.toString());
    }
}
