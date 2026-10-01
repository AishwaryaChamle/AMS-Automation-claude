package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_013: Verify onboarding request raised for candidate age >= 50
 * Expected: Request should move to LOP approval bucket successfully
 */
public class TC_ONB_013 extends BaseTest {

    @Test
    public void requestForCandidateAged50OrMoreGoesToLop() throws Exception {
        Lead lead = onboarding.newLead(TestData.leadAged(52));

        // HOB raises the onboarding request
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        // Where did the request go?
        boolean inTarget = onboarding.isPendingWith(Config.lop(), lead);
        boolean inOther = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);

        actual("HOB raised the request for " + lead.fullName() + " (age " + lead.age() + ", ARI " + ariTotal + "). In LOP approval tray: "
                + inTarget + ", in Sr MBD approval tray: " + inOther);
        Assert.assertTrue(inTarget, "Request is not in the LOP approval tray");
        Assert.assertFalse(inOther, "Request is also in the Sr MBD approval tray");
    }
}
