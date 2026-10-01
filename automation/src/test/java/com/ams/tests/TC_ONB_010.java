package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_010: Verify approval flow for ARI >= 30
 * Expected: After LOP approval, request should move to Agency Admin approval bucket
 */
public class TC_ONB_010 extends BaseTest {

    @Test
    public void approvalFlowForAri30OrMoreGoesLopAgencyAdmin() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        Assert.assertTrue(ariTotal >= 30, "Test data should give ARI >= 30 but gave " + ariTotal);

        // LOP approves -> Agency Admin
        onboarding.approve(Config.lop(), lead);
        boolean inAgencyAdminTray = onboarding.isPendingWith(Config.agencyAdmin(), lead);

        actual("ARI " + ariTotal + ". After LOP approval in Agency Admin tray: " + inAgencyAdminTray);
        Assert.assertTrue(inAgencyAdminTray, "Request did not move to Agency Admin after LOP approval");
    }
}
