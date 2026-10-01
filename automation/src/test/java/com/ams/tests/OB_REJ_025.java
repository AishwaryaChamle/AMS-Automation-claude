package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_025: Verify LOP rejection for an ARI > 30 request returns it to the original HOB/AS/LOP requester.
 * Expected: Original requester receives the rejected request and can correct and resubmit it.
 */
public class OB_REJ_025 extends BaseTest {

    @Test
    public void lopRejectionForAri30OrMoreReturnsToRequester() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);

        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");
        String status = onboarding.statusFor(Config.hob(), lead);
        String resubmitted = onboarding.resubmit(Config.hob(), lead);

        actual("After LOP rejection - status for HOB: " + status + ". Resubmission: '" + resubmitted + "'");
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Request did not return to HOB");
        Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "HOB could not resubmit: " + resubmitted);
    }
}
