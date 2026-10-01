package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_011: Verify lead status after rejection at any stage
 * Expected: Lead status should be displayed as "Rejected" and no further actions should be available
 */
public class TC_REC_APP_011 extends BaseTest {

    @Test
    public void rejectedLeadShowsRejectedStatusWithNoFurtherActions() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        Flows.expectSuccess(flows.rejectInTray(Config.supervisor(), lead), "Supervisor rejection");

        // HOB opens the campaign in Recruitment Leads Mgmt and checks the rejected lead
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        String hobRow = leadPage.rowText(lead.nic());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());
        boolean editable = leadPage.hasEditIcon(lead.nic());

        actual("HOB row: " + hobRow + " (edit icon: " + editable + ", Proceed enabled: " + proceedEnabled + ")");
        Assert.assertTrue(hobRow.contains("Rejected"), "HOB list does not show Rejected");
        Assert.assertFalse(proceedEnabled, "Proceed is still available for a rejected lead");
        Assert.assertFalse(editable, "Edit action is still available for a rejected lead");
    }
}
