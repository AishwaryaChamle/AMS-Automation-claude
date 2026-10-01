package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_013: Verify the original requester can resubmit an onboarding request after Agency Admin rejection.
 * Expected: HOB/AS/LOP can correct the request and raise it again for the applicable approval flow.
 */
public class OB_REJ_013 extends BaseTest {

    @Test
    public void requesterResubmitsAfterAgencyAdminRejection() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approve(Config.lop(), lead);
        onboarding.reject(Config.agencyAdmin(), lead, "Rejected by Agency Admin - automation");

        String resubmitted = onboarding.resubmit(Config.hob(), lead);
        boolean withLop = onboarding.isPendingWith(Config.lop(), lead);

        actual("Resubmission after Agency Admin rejection: '" + resubmitted + "'. With LOP again: " + withLop);
        Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "Resubmission failed: " + resubmitted);
        Assert.assertTrue(withLop, "Resubmitted request does not follow the approval flow (not with LOP)");
    }
}
