package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_007: Verify approval notifications are sent only to the respective pending approver.
 * Expected: TRSM receives notification when the application is pending with TRSM, and AA receives notification when it reaches AA.
 */
public class INT_NOTIF_007 extends BaseTest {

    @Test
    public void approvalNotificationGoesOnlyToPendingApprover() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // While pending with the Supervisor: Supervisor is notified, Agency Admin is not
        boolean supervisorNotified = flows.notificationsOf(Config.supervisor()).contains(Flows.pendingNotification(lead));
        boolean adminNotifiedEarly = flows.notificationsOf(Config.agencyAdmin()).contains(Flows.pendingNotification(lead));

        // After the Supervisor approves: Agency Admin is notified
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Supervisor approval");
        boolean adminNotifiedLater = flows.notificationsOf(Config.agencyAdmin()).contains(Flows.pendingNotification(lead));

        actual("Supervisor notified while pending: " + supervisorNotified + "; Agency Admin notified too early: " + adminNotifiedEarly
                + "; Agency Admin notified after Supervisor approval: " + adminNotifiedLater);
        Assert.assertTrue(supervisorNotified, "Supervisor was not notified");
        Assert.assertFalse(adminNotifiedEarly, "Agency Admin was notified before the Supervisor approved");
        Assert.assertTrue(adminNotifiedLater, "Agency Admin was not notified after Supervisor approval");
    }
}
