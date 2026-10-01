package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_004: Verify lead workflow progression.
 * Expected: Lead proceeds to the next permitted workflow state.
 */
public class RLC_HA_004 extends BaseTest {

    @Test
    public void leadProceedsToNextWorkflowState() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        String row = approvalTray.rowText(lead.fullName());

        actual("After HOB approval the lead is in the Supervisor tray: " + row);
        Assert.assertNotNull(row, "Lead did not move to the next workflow state");
    }
}
