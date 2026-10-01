package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_AT_004: Verify approved workflow transition.
 * Expected: Lead transitions to the approved workflow state.
 */
public class RLC_AT_004 extends BaseTest {

    @Test
    public void approvedLeadTransitionsToApprovedState() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Supervisor approval");
        flows.findInApprovalTray(Config.supervisor(), "Completed", lead.fullName());
        String row = approvalTray.rowText(lead.fullName());

        actual("Supervisor Completed tab row: " + row);
        Assert.assertNotNull(row, "Approved lead is not in the approved (Completed) state");
    }
}
