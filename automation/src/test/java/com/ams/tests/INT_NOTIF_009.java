package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_009: Verify application rejection at AA stops further processing.
 * Expected: Application remains rejected and no further approval notification is generated. HOB receives AA rejection notification.
 */
public class INT_NOTIF_009 extends BaseTest {

    @Test
    public void agencyAdminRejectionStopsFurtherProcessing() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        // Agency Admin rejects the lead
        Flows.expectSuccess(flows.rejectInTray(Config.agencyAdmin(), lead), "Agency Admin rejection");

        // HOB must receive a rejection notification naming the Agency Admin
        String notifications = flows.notificationsOf(Config.hob());
        String expected = Flows.rejectedNotification(lead, "Agency Admin");
        boolean notified = notifications.contains(expected);

        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        boolean stillPending = approvalTray.isListed(lead.fullName());
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());

        actual("HOB notified of Agency Admin rejection: " + notified + "; still pending at Agency Admin: " + stillPending + "; Proceed enabled: " + proceedEnabled);
        Assert.assertTrue(notified, "HOB did not receive the Agency Admin rejection notification");
        Assert.assertFalse(stillPending, "Rejected lead is still pending approval");
        Assert.assertFalse(proceedEnabled, "Rejected lead can still be proceeded");
    }
}
