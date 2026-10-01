package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AO_NOTIF_008: Verify notification status is correctly recorded and duplicate notifications are not generated for a single workflow transition
 * Expected: Each workflow transition generates the expected notification only once.
 */
public class AO_NOTIF_008 extends BaseTest {

    @Test
    public void oneNotificationPerWorkflowTransition() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        int srMbdCount = OnboardingFlows.occurrences(flows.notificationsOf(OnboardingFlows.smbd()), OnboardingFlows.pendingNotification(lead));

        onboarding.approve(OnboardingFlows.smbd(), lead);
        int lopCount = OnboardingFlows.occurrences(flows.notificationsOf(Config.lop()), OnboardingFlows.pendingNotification(lead));
        int requesterApprovedCount = OnboardingFlows.occurrences(flows.notificationsOf(Config.hob()), OnboardingFlows.approvedNotification(lead));

        actual("Pending notifications - Sr MBD: " + srMbdCount + ", LOP: " + lopCount + ". Approval notifications to HOB after Sr MBD approval: " + requesterApprovedCount);
        Assert.assertEquals(srMbdCount, 1, "Sr MBD notification count for raising the request");
        Assert.assertEquals(lopCount, 1, "LOP notification count for the Sr MBD approval");
        Assert.assertTrue(requesterApprovedCount <= 1, "Duplicate approval notifications sent to the requester: " + requesterApprovedCount);
    }
}
