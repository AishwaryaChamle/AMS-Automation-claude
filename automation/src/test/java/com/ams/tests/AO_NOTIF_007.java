package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AO_NOTIF_007: Verify notifications are sent only to the correct users based on the workflow stage
 * Expected: Requester receives notification when application gets rejeceted, while the role responsible for the current approval receives the action notification; unrelated users do not receive the notification
 */
public class AO_NOTIF_007 extends BaseTest {

    @Test
    public void notificationsReachOnlyTheResponsibleUsers() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        // While the request waits for the Sr MBD, only the Sr MBD gets the action notification
        boolean srMbdPending = flows.notificationsOf(OnboardingFlows.smbd()).contains(OnboardingFlows.pendingNotification(lead));
        boolean lopPending = flows.notificationsOf(Config.lop()).contains(lead.fullName());
        boolean agencyAdminPending = flows.notificationsOf(Config.agencyAdmin()).contains(lead.fullName());

        // After the Sr MBD rejects, the requester gets the rejection notification; LOP and Agency Admin get nothing
        onboarding.reject(OnboardingFlows.smbd(), lead, "Rejected by Sr MBD - automation");
        boolean requesterRejected = flows.notificationsOf(Config.hob()).contains(OnboardingFlows.rejectedNotification(lead));
        boolean lopAfter = flows.notificationsOf(Config.lop()).contains(lead.fullName());
        boolean agencyAdminAfter = flows.notificationsOf(Config.agencyAdmin()).contains(lead.fullName());

        actual("Pending at Sr MBD - Sr MBD notified: " + srMbdPending + ", LOP: " + lopPending + ", Agency Admin: " + agencyAdminPending
                + ". After rejection - requester notified: " + requesterRejected + ", LOP: " + lopAfter + ", Agency Admin: " + agencyAdminAfter);
        Assert.assertTrue(srMbdPending, "Sr MBD did not get the action notification");
        Assert.assertFalse(lopPending, "LOP was notified although the request is with the Sr MBD");
        Assert.assertFalse(agencyAdminPending, "Agency Admin was notified although the request is with the Sr MBD");
        Assert.assertTrue(requesterRejected, "Requester did not get the rejection notification");
        Assert.assertFalse(lopAfter, "LOP was notified about a request it never received");
        Assert.assertFalse(agencyAdminAfter, "Agency Admin was notified about a request it never received");
    }
}
