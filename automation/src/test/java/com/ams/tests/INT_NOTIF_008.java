package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * INT_NOTIF_008: Verify AA does not receive an approval notification before TRSM approves the application.
 * Expected: AA notification is triggered only after successful TRSM approval and application movement to AA bucket.
 */
public class INT_NOTIF_008 extends BaseTest {

    @Test
    public void agencyAdminNotNotifiedBeforeSupervisorApproval() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // The lead is still pending with the Supervisor
        boolean adminNotified = flows.notificationsOf(Config.agencyAdmin()).contains(Flows.pendingNotification(lead));
        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        boolean inAdminBucket = approvalTray.isListed(lead.fullName());

        actual("Agency Admin notified before Supervisor approval: " + adminNotified + "; lead in Agency Admin bucket: " + inAdminBucket);
        Assert.assertFalse(adminNotified, "Agency Admin was notified before Supervisor approval");
        Assert.assertFalse(inAdminBucket, "Lead reached Agency Admin before Supervisor approval");
    }
}
