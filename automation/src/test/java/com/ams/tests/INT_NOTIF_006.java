package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_006: Verify HOB can identify the exact approval level at which the application was rejected.
 * Expected: Notification clearly identifies whether rejection occurred at TRSM or AA level.
 */
public class INT_NOTIF_006 extends BaseTest {

    @Test
    public void hobCanIdentifyRejectionLevelFromNotification() throws Exception {
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

        boolean wronglyNamesSupervisor = notifications.contains(Flows.rejectedNotification(lead, "Immediate Supervisor"));

        actual("Notification names Agency Admin as rejection level: " + notified + "; wrongly names Immediate Supervisor: " + wronglyNamesSupervisor);
        Assert.assertTrue(notified, "Notification does not identify Agency Admin as the rejection level");
        Assert.assertFalse(wronglyNamesSupervisor, "Notification names the wrong rejection level");
    }
}
