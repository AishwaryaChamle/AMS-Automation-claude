package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_003: Verify notification is sent to HOB when TRSM rejects the application.
 * Expected: Application is marked Rejected at TRSM level and HOB receives a notification identifying TRSM as the rejection level.
 */
public class INT_NOTIF_003 extends BaseTest {

    @Test
    public void hobNotifiedWithLevelWhenSupervisorRejects() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor rejects the lead
        Flows.expectSuccess(flows.rejectInTray(Config.supervisor(), lead), "Supervisor rejection");

        // HOB must receive a rejection notification naming the Immediate Supervisor
        String notifications = flows.notificationsOf(Config.hob());
        String expected = Flows.rejectedNotification(lead, "Immediate Supervisor");
        boolean notified = notifications.contains(expected);

        flows.findInApprovalTray(Config.supervisor(), "Rejected", lead.fullName());
        boolean inRejectedTab = approvalTray.isListed(lead.fullName());

        actual("HOB notified ('" + expected + "'): " + notified + ". Lead in Supervisor's Rejected tab: " + inRejectedTab);
        Assert.assertTrue(inRejectedTab, "Lead is not marked Rejected at Supervisor level");
        Assert.assertTrue(notified, "HOB did not receive the Supervisor rejection notification");
    }
}
