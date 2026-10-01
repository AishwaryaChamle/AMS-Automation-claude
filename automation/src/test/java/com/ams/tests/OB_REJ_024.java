package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_024: Verify an ARI > 30 request skips SMBD approval even after subsequent rejection/resubmission cycles.
 * Expected: Request goes directly to LOP; SMBD approval is not introduced into the workflow.
 */
public class OB_REJ_024 extends BaseTest {

    @Test
    public void ari30OrMoreResubmissionSkipsSrMbd() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        Assert.assertTrue(ariTotal >= 30, "Test data should give ARI >= 30 but gave " + ariTotal);

        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");
        onboarding.resubmit(Config.hob(), lead);
        boolean withLop = onboarding.isPendingWith(Config.lop(), lead);
        boolean withSrMbd = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);

        actual("ARI " + ariTotal + ". After rejection and resubmission - with LOP: " + withLop + ", with Sr MBD: " + withSrMbd);
        Assert.assertTrue(withLop, "Resubmitted request is not with LOP");
        Assert.assertFalse(withSrMbd, "Resubmitted request went to the Sr MBD");
    }
}
