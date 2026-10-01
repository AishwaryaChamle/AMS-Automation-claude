package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AO_NOTIF_004: Verify rejection notification is triggered when SMBD rejects the application
 * Expected: Original requester receives the rejection notification containing the applicant name.
 */
public class AO_NOTIF_004 extends BaseTest {

    @Test
    public void requesterNotifiedWhenSrMbdRejects() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        onboarding.reject(OnboardingFlows.smbd(), lead, "Rejected by Sr MBD - automation");

        String notifications = flows.notificationsOf(Config.hob());

        actual("HOB notifications contain '" + OnboardingFlows.rejectedNotification(lead) + "': "
                + notifications.contains(OnboardingFlows.rejectedNotification(lead)));
        Assert.assertTrue(notifications.contains(OnboardingFlows.rejectedNotification(lead)), "Requester was not notified of the Sr MBD rejection");
    }
}
