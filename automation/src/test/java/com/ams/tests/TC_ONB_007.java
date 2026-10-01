package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_007: Verify HOB can raise onboarding request when ARI >= 30
 * Expected: Request should be raised successfully and should appear in LOP approval bucket
 */
public class TC_ONB_007 extends BaseTest {

    @Test
    public void hobRaisesRequestWithAri30OrMoreGoesToLop() throws Exception {
        Lead lead = onboarding.newLead();

        // HOB raises the onboarding request
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        Assert.assertTrue(ariTotal >= 30, "Test data should give ARI >= 30 but gave " + ariTotal);

        // Where did the request go?
        boolean inTarget = onboarding.isPendingWith(Config.lop(), lead);
        boolean inOther = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);

        actual("HOB raised the request for " + lead.fullName() + " (age " + lead.age() + ", ARI " + ariTotal + "). In LOP approval tray: "
                + inTarget + ", in Sr MBD approval tray: " + inOther);
        Assert.assertTrue(inTarget, "Request is not in the LOP approval tray");
        Assert.assertFalse(inOther, "Request is also in the Sr MBD approval tray");
    }
}
