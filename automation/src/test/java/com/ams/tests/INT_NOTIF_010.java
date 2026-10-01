package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_010: Verify approval/rejection notification contains the correct application/lead details.
 * Expected: Notification contains the correct applicant/lead reference and current approval/rejection status.
 */
public class INT_NOTIF_010 extends BaseTest {

    @Test
    public void notificationContainsCorrectLeadDetails() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Pending notification to the Supervisor names the correct lead and status
        boolean pendingCorrect = flows.notificationsOf(Config.supervisor()).contains(Flows.pendingNotification(lead));

        // Approval notification to HOB names the correct lead and status
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Supervisor approval");
        String expectedApproval = Flows.approvedNotification(lead, "Immediate Supervisor");
        boolean approvalCorrect = flows.notificationsOf(Config.hob()).contains(expectedApproval);

        actual("Supervisor got '" + Flows.pendingNotification(lead) + "': " + pendingCorrect + "; HOB got '" + expectedApproval + "': " + approvalCorrect);
        Assert.assertTrue(pendingCorrect, "Pending notification does not contain the correct lead");
        Assert.assertTrue(approvalCorrect, "Approval notification does not contain the correct lead / status");
    }
}
