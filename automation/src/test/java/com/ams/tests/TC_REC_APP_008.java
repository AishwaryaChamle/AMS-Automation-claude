package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_008: Verify rejection by HOB during interview approval
 * Expected: Lead should be rejected immediately and no further approval stages should be triggered
 */
public class TC_REC_APP_008 extends BaseTest {

    @Test
    public void hobRejectionStopsFurtherApprovals() throws Exception {
        // HOB creates a lead and rejects it during interview approval
        Lead lead = TestData.adultLead();
        String message = flows.hobCreatesAndRejectsLead(lead);

        // The lead must not reach the Immediate Supervisor
        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        boolean atSupervisor = approvalTray.isListed(lead.fullName());

        // HOB lead list shows the rejected state and no active Proceed
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        String row = leadPage.rowText(lead.nic());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());

        actual("HOB pop-up: '" + message + "'. Listed at Supervisor: " + atSupervisor + ". HOB lead row: " + row);
        Assert.assertTrue(message.contains("Rejected Successfully"), "HOB rejection failed: " + message);
        Assert.assertFalse(atSupervisor, "Rejected lead still went to the Supervisor");
        Assert.assertFalse(proceedEnabled, "Rejected lead can still be proceeded");
    }
}
