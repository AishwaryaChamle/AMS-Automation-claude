package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * AO_NOTIF_006: Verify rejection notification is triggered when AA rejects the application
 * Expected: Original requester receives the rejection notification and the application is returned to the requester for the applicable next action
 */
public class AO_NOTIF_006 extends BaseTest {

    @Test
    public void requesterNotifiedWhenAgencyAdminRejects() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approve(Config.lop(), lead);
        onboarding.reject(Config.agencyAdmin(), lead, "Rejected by Agency Admin - automation");

        String notifications = flows.notificationsOf(Config.hob());
        String status = onboarding.statusFor(Config.hob(), lead);
        List<String> actions = onboardingPage.actions(lead.fullName());

        actual("HOB notifications contain '" + OnboardingFlows.rejectedNotification(lead) + "': "
                + notifications.contains(OnboardingFlows.rejectedNotification(lead)) + ". Status: " + status + ", actions: " + actions);
        Assert.assertTrue(notifications.contains(OnboardingFlows.rejectedNotification(lead)), "Requester was not notified of the Agency Admin rejection");
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Application is not back with the requester");
        Assert.assertTrue(actions.contains("Edit"), "Requester cannot correct the application");
    }
}
