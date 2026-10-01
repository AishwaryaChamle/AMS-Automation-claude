package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_AT_005: Verify rejected workflow transition.
 * Expected: Lead transitions to the rejected workflow state.
 */
public class RLC_AT_005 extends BaseTest {

    @Test
    public void rejectedLeadTransitionsToRejectedState() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        Flows.expectSuccess(flows.rejectInTray(Config.supervisor(), lead), "Supervisor rejection");
        flows.findInApprovalTray(Config.supervisor(), "Rejected", lead.fullName());
        boolean inRejected = approvalTray.isListed(lead.fullName());
        approvalTray.openTab("In Progress");
        approvalTray.search(lead.fullName());
        boolean stillInProgress = approvalTray.isListed(lead.fullName());

        actual("In Rejected tab: " + inRejected + "; still In Progress: " + stillInProgress);
        Assert.assertTrue(inRejected, "Lead is not in the Rejected state");
        Assert.assertFalse(stillInProgress, "Rejected lead is still In Progress");
    }
}
