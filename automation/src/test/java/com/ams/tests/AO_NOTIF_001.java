package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AO_NOTIF_001: Verify notifications are triggered when an onboarding application moves to the next approval role
 * Expected: Notification is triggered to the next-level approver when the application moves to the next approval stage
 */
public class AO_NOTIF_001 extends BaseTest {

    @Test
    public void nextApproverNotifiedWhenRequestIsRaised() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        String notifications = flows.notificationsOf(OnboardingFlows.smbd());

        actual("Sr MBD notifications contain '" + OnboardingFlows.pendingNotification(lead) + "': "
                + notifications.contains(OnboardingFlows.pendingNotification(lead)));
        Assert.assertTrue(notifications.contains(OnboardingFlows.pendingNotification(lead)), "Sr MBD was not notified");
    }
}
