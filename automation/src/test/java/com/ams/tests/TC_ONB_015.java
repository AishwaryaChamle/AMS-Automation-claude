package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_015: Verify approval flow for candidate age >= 50
 * Expected: After LOP approval, request should move to Agency Admin approval bucket
 */
public class TC_ONB_015 extends BaseTest {

    @Test
    public void approvalFlowForCandidateAged50OrMore() throws Exception {
        Lead lead = onboarding.newLead(TestData.leadAged(52));
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        // LOP approves -> Agency Admin
        onboarding.approve(Config.lop(), lead);
        boolean inAgencyAdminTray = onboarding.isPendingWith(Config.agencyAdmin(), lead);

        actual("Candidate aged " + lead.age() + ", ARI " + ariTotal + ". After LOP approval in Agency Admin tray: " + inAgencyAdminTray);
        Assert.assertTrue(inAgencyAdminTray, "Request did not move to Agency Admin after LOP approval");
    }
}
