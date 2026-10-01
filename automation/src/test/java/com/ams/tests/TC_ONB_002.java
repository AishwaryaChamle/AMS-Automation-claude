package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_002: Verify AS can raise onboarding request when ARI < 30
 * Expected: Request should be raised successfully and should appear in Sr MBD approval bucket
 */
public class TC_ONB_002 extends BaseTest {

    @Test
    public void agencySupervisorRaisesRequestWithAriBelow30GoesToSrMbd() throws Exception {
        Lead lead = onboarding.newLead();

        // Agency Supervisor raises the onboarding request
        int ariTotal = onboarding.raiseRequest(Config.agencySupervisor(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);

        // Where did the request go?
        boolean inTarget = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);
        boolean inOther = onboarding.isPendingWith(Config.lop(), lead);

        actual("Agency Supervisor raised the request for " + lead.fullName() + " (age " + lead.age() + ", ARI " + ariTotal + "). In Sr MBD approval tray: "
                + inTarget + ", in LOP approval tray: " + inOther);
        Assert.assertTrue(inTarget, "Request is not in the Sr MBD approval tray");
        Assert.assertFalse(inOther, "Request is also in the LOP approval tray");
    }
}
