package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_005: Verify Agency Admin approval flow for candidate age < 22
 * Expected: After TRSM approval, the lead should appear in Agency Admin approval bucket
 */
public class TC_REC_APP_005 extends BaseTest {

    @Test
    public void youngLeadReachesAgencyAdminBucketAfterSupervisorApproval() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        // The lead must now be pending in the Agency Admin's Approval Tray
        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        String row = approvalTray.rowText(lead.fullName());

        actual("Agency Admin Approval Tray row for " + lead.fullName() + ": " + row);
        Assert.assertNotNull(row, "Lead is not in the Agency Admin approval bucket");
    }
}
