package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_AT_003: Verify lead rejection.
 * Expected: Rejection succeeds and rejection status is updated.
 */
public class RLC_AT_003 extends BaseTest {

    @Test
    public void approverRejectsLead() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        String message = flows.rejectInTray(Config.supervisor(), lead);
        flows.findInApprovalTray(Config.supervisor(), "Rejected", lead.fullName());
        boolean rejected = approvalTray.isListed(lead.fullName());

        actual("Rejection pop-up: '" + message + "'. Listed under Rejected: " + rejected);
        Assert.assertTrue(message.contains("Rejected Successfully"), "Rejection failed: " + message);
        Assert.assertTrue(rejected, "Rejection status not updated");
    }
}
