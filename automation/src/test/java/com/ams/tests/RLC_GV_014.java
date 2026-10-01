package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_014: Verify notification remarks behavior.
 * Expected: Notification is validated against the current implementation; remarks are not expected within the notification.
 */
public class RLC_GV_014 extends BaseTest {

    @Test
    public void notificationsHaveNoRemarks() throws Exception {
        // HOB approves with a unique remark
        Lead lead = TestData.adultLead();
        String remark = "Approved by HOB for interview - automation";
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        String notifications = flows.notificationsOf(Config.supervisor());
        boolean notified = notifications.contains(Flows.pendingNotification(lead));
        boolean containsRemark = notifications.contains(remark);

        actual("Supervisor notification generated: " + notified + "; notification contains the remark: " + containsRemark);
        Assert.assertTrue(notified, "No approval notification generated");
        Assert.assertFalse(containsRemark, "Notification contains the remark");
    }
}
