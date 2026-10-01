package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_002: Verify notification is sent to AA when TRSM approves the application.
 * Expected: Application moves to AA bucket and AA receives the approval notification.
 */
public class INT_NOTIF_002 extends BaseTest {

    @Test
    public void agencyAdminNotifiedWhenSupervisorApproves() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        String notifications = flows.notificationsOf(Config.agencyAdmin());
        boolean notified = notifications.contains(Flows.pendingNotification(lead));
        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        boolean inBucket = approvalTray.isListed(lead.fullName());

        actual("Agency Admin notified: " + notified + ". Lead in Agency Admin bucket: " + inBucket);
        Assert.assertTrue(inBucket, "Lead did not move to the Agency Admin bucket");
        Assert.assertTrue(notified, "Agency Admin did not receive the pending-approval notification");
    }
}
