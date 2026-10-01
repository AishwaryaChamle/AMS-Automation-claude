package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_006: Verify final approval by Agency Admin for candidate age < 22
 * Expected: Agency Admin should be able to approve the lead successfully
 */
public class TC_REC_APP_006 extends BaseTest {

    @Test
    public void agencyAdminGivesFinalApprovalForYoungLead() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        // Agency Admin gives the final approval
        String message = flows.approveInTray(Config.agencyAdmin(), lead);

        actual("Agency Admin approval pop-up: '" + message + "'");
        Assert.assertTrue(message.contains("Lead Approved Successfully"), "Agency Admin approval failed: " + message);
    }
}
