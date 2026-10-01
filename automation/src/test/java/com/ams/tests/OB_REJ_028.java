package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_028: Verify the original requester is retained throughout all rejection and resubmission cycles.
 * Expected: Whether HOB, AS, or LOP originally raised the request, every rejection is returned to that same requester for resubmission.
 */
public class OB_REJ_028 extends BaseTest {

    @Test
    public void rejectionAlwaysReturnsToOriginalRequester() throws Exception {
        // The Agency Supervisor raises the request (HOB is not the requester)
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.agencySupervisor(), lead, TestData.HIGH_ARI);

        StringBuilder rounds = new StringBuilder();
        for (int round = 1; round <= 2; round++) {
            onboarding.reject(Config.lop(), lead, "LOP rejection " + round + " - automation");
            String asStatus = onboarding.statusFor(Config.agencySupervisor(), lead);
            String hobStatus = onboarding.statusFor(Config.hob(), lead);
            boolean hobCanEdit = onboardingPage.isListed(lead.fullName()) && onboardingPage.actions(lead.fullName()).contains("Edit");
            String resubmitted = onboarding.resubmit(Config.agencySupervisor(), lead);
            rounds.append("Round ").append(round).append(": AS status ").append(asStatus).append(", HOB status ").append(hobStatus)
                    .append(" (can edit: ").append(hobCanEdit).append("), AS resubmission '").append(resubmitted).append("'. ");
            Assert.assertEquals(asStatus, OnboardingFlows.STATUS_REJECTED, "Round " + round + ": request did not return to the AS");
            Assert.assertFalse(OnboardingFlows.STATUS_REJECTED.equals(hobStatus) && hobCanEdit, "Round " + round + ": request was returned to HOB instead of the AS");
            Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "Round " + round + ": AS could not resubmit");
        }
        actual(rounds.toString());
    }
}
