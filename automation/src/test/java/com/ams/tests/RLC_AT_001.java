package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_AT_001: Verify Agency Admin approval workflow visibility.
 * Expected: The lead appears in the Agency Admin approval workflow.
 */
public class RLC_AT_001 extends BaseTest {

    @Test
    public void leadVisibleInAgencyAdminApprovalWorkflow() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        String row = approvalTray.rowText(lead.fullName());

        actual("Agency Admin Approval Tray row: " + row);
        Assert.assertNotNull(row, "Lead is not in the Agency Admin approval workflow");
    }
}
