package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_001: Verify notification is sent to TRSM when HOB approves the interview status and the application moves to TRSM approval.
 * Expected: Application moves to TRSM bucket and TRSM receives the approval notification.
 */
public class INT_NOTIF_001 extends BaseTest {

    @Test
    public void supervisorNotifiedWhenHobApprovesInterview() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor checks notifications and the Approval Tray
        String notifications = flows.notificationsOf(Config.supervisor());
        boolean notified = notifications.contains(Flows.pendingNotification(lead));
        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        boolean inBucket = approvalTray.isListed(lead.fullName());

        actual("Supervisor notified ('" + Flows.pendingNotification(lead) + "'): " + notified + ". Lead in Supervisor bucket: " + inBucket);
        Assert.assertTrue(inBucket, "Lead did not move to the Supervisor bucket");
        Assert.assertTrue(notified, "Supervisor did not receive the pending-approval notification");
    }
}
