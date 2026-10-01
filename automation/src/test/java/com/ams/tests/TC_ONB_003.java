package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_003: Verify LOP can raise onboarding request when ARI < 30
 * Expected: Request should be raised successfully and should appear in Sr MBD approval bucket
 */
public class TC_ONB_003 extends BaseTest {

    @Test
    public void lopRaisesRequestWithAriBelow30GoesToSrMbd() throws Exception {
        Lead lead = onboarding.newLead();

        // LOP raises the onboarding request
        int ariTotal = onboarding.raiseRequest(Config.lop(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);

        // Where did the request go?
        boolean inTarget = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);
        boolean inOther = onboarding.isPendingWith(Config.agencyAdmin(), lead);

        actual("LOP raised the request for " + lead.fullName() + " (age " + lead.age() + ", ARI " + ariTotal + "). In Sr MBD approval tray: "
                + inTarget + ", in Agency Admin approval tray: " + inOther);
        Assert.assertTrue(inTarget, "Request is not in the Sr MBD approval tray");
        Assert.assertFalse(inOther, "Request is also in the Agency Admin approval tray");
    }
}
