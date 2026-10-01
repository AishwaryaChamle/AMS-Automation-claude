package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AO_NOTIF_002: Verify notifications are triggered when SMBD approves and the application moves to LOP
 * Expected: LOP receives an approval action notification.
 */
public class AO_NOTIF_002 extends BaseTest {

    @Test
    public void lopNotifiedWhenSrMbdApproves() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        onboarding.approve(OnboardingFlows.smbd(), lead);

        String notifications = flows.notificationsOf(Config.lop());

        actual("LOP notifications contain '" + OnboardingFlows.pendingNotification(lead) + "': "
                + notifications.contains(OnboardingFlows.pendingNotification(lead)));
        Assert.assertTrue(notifications.contains(OnboardingFlows.pendingNotification(lead)), "LOP was not notified");
    }
}
