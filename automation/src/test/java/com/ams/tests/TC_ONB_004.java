package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_004: Verify approval flow for ARI < 30
 * Expected: After Sr MBD approval, request should move to LOP approval bucket and then to Agency Admin approval bucket
 */
public class TC_ONB_004 extends BaseTest {

    @Test
    public void approvalFlowForAriBelow30GoesSrMbdLopAgencyAdmin() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);

        // Sr MBD approves -> LOP; LOP approves -> Agency Admin
        onboarding.approve(OnboardingFlows.smbd(), lead);
        boolean inLopTray = onboarding.isPendingWith(Config.lop(), lead);
        onboarding.approve(Config.lop(), lead);
        boolean inAgencyAdminTray = onboarding.isPendingWith(Config.agencyAdmin(), lead);

        actual("ARI " + ariTotal + ". After Sr MBD approval in LOP tray: " + inLopTray
                + ". After LOP approval in Agency Admin tray: " + inAgencyAdminTray);
        Assert.assertTrue(inLopTray, "Request did not move to LOP after Sr MBD approval");
        Assert.assertTrue(inAgencyAdminTray, "Request did not move to Agency Admin after LOP approval");
    }
}
