package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AO_NOTIF_003: Verify notifications are triggered when LOP approves and the application moves to AA
 * Expected: AA receives an approval action notification.
 */
public class AO_NOTIF_003 extends BaseTest {

    @Test
    public void agencyAdminNotifiedWhenLopApproves() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approve(Config.lop(), lead);

        String notifications = flows.notificationsOf(Config.agencyAdmin());

        actual("Agency Admin notifications contain '" + OnboardingFlows.pendingNotification(lead) + "': "
                + notifications.contains(OnboardingFlows.pendingNotification(lead)));
        Assert.assertTrue(notifications.contains(OnboardingFlows.pendingNotification(lead)), "Agency Admin was not notified");
    }
}
