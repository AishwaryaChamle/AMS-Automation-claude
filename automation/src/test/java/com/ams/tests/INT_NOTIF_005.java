package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_005: Verify notification is sent to HOB when AA rejects the application.
 * Expected: Application is marked Rejected at AA level and HOB receives a notification identifying AA as the rejection level.
 */
public class INT_NOTIF_005 extends BaseTest {

    @Test
    public void hobNotifiedWithLevelWhenAgencyAdminRejects() throws Exception {
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

        flows.findInApprovalTray(Config.agencyAdmin(), "Rejected", lead.fullName());
        boolean inRejectedTab = approvalTray.isListed(lead.fullName());

        actual("HOB notified ('" + expected + "'): " + notified + ". Lead in Agency Admin Rejected tab: " + inRejectedTab);
        Assert.assertTrue(inRejectedTab, "Lead is not marked Rejected at Agency Admin level");
        Assert.assertTrue(notified, "HOB did not receive the Agency Admin rejection notification");
    }
}
