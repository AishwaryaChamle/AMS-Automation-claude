package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_004: Verify HOB receives the notification when TRSM rejects the application.
 * Expected: HOB gets the notification when TRSM rejects the Lead.
 */
public class INT_NOTIF_004 extends BaseTest {

    @Test
    public void hobReceivesNotificationWhenSupervisorRejects() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor rejects the lead
        Flows.expectSuccess(flows.rejectInTray(Config.supervisor(), lead), "Supervisor rejection");

        // HOB must receive a rejection notification naming the Immediate Supervisor
        String notifications = flows.notificationsOf(Config.hob());
        String expected = Flows.rejectedNotification(lead, "Immediate Supervisor");
        boolean notified = notifications.contains(expected);

        actual("HOB notification '" + expected + "' received: " + notified);
        Assert.assertTrue(notified, "HOB did not receive the rejection notification");
    }
}
