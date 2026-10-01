package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_013: Verify approval/rejection notification implementation.
 * Expected: Implemented approval/rejection notifications can be validated; Email/SMS dispatch is excluded because it is not currently done.
 */
public class RLC_GV_013 extends BaseTest {

    @Test
    public void approvalNotificationsImplemented() throws Exception {
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
