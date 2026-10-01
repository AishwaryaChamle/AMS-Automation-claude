package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_004: Verify interview approval flow for candidate age < 22 after HOB approval
 * Expected: After HOB approval, the lead should appear in TRSM interview approval bucket
 */
public class TC_REC_APP_004 extends BaseTest {

    @Test
    public void youngLeadReachesSupervisorBucketAfterHobApproval() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // The lead must now be pending in the Immediate Supervisor's Approval Tray
        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        String row = approvalTray.rowText(lead.fullName());

        actual("Supervisor Approval Tray row for " + lead.fullName() + " (age " + lead.age() + "): " + row);
        Assert.assertNotNull(row, "Lead is not in the Supervisor's approval bucket");
    }
}
