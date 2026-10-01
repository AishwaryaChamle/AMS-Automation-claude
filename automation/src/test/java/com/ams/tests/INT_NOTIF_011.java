package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_011: Verify rejection at each approval stage: TRSM rejection and AA rejection.
 * Expected: In either case, application is rejected and HOB receives notification specifying the exact rejection level.
 */
public class INT_NOTIF_011 extends BaseTest {

    @Test
    public void hobNotifiedOfRejectionAtEachApprovalStage() throws Exception {
        // Lead 1 (above 22) is rejected by the Immediate Supervisor
        Lead first = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(first), "HOB approval of lead 1");
        Flows.expectSuccess(flows.rejectInTray(Config.supervisor(), first), "Supervisor rejection");

        // Lead 2 (under 22) is rejected by the Agency Admin
        Lead second = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(second), "HOB approval of lead 2");
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), second), "Supervisor approval of lead 2");
        Flows.expectSuccess(flows.rejectInTray(Config.agencyAdmin(), second), "Agency Admin rejection");

        String notifications = flows.notificationsOf(Config.hob());
        boolean supervisorLevel = notifications.contains(Flows.rejectedNotification(first, "Immediate Supervisor"));
        boolean adminLevel = notifications.contains(Flows.rejectedNotification(second, "Agency Admin"));

        actual("HOB notification for Supervisor rejection: " + supervisorLevel + "; for Agency Admin rejection: " + adminLevel);
        Assert.assertTrue(supervisorLevel, "No notification naming the Supervisor rejection");
        Assert.assertTrue(adminLevel, "No notification naming the Agency Admin rejection");
    }
}
