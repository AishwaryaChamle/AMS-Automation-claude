package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_AT_002: Verify lead approval.
 * Expected: Approval succeeds and approval status is updated.
 */
public class RLC_AT_002 extends BaseTest {

    @Test
    public void approverApprovesLead() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        String message = flows.approveInTray(Config.agencyAdmin(), lead);
        flows.findInApprovalTray(Config.agencyAdmin(), "Completed", lead.fullName());
        boolean completed = approvalTray.isListed(lead.fullName());

        actual("Approval pop-up: '" + message + "'. Listed under Completed: " + completed);
        Assert.assertTrue(message.contains("Lead Approved Successfully"), "Approval failed: " + message);
        Assert.assertTrue(completed, "Approval status not updated (not in Completed)");
    }
}
