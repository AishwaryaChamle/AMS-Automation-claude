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
 * AO_NOTIF_005: Verify rejection notification is triggered when LOP rejects the application
 * Expected: Original requester receives the rejection notification and the application is available to the requester for correction/re-submission
 */
public class AO_NOTIF_005 extends BaseTest {

    @Test
    public void requesterNotifiedWhenLopRejects() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");

        String notifications = flows.notificationsOf(Config.hob());
        String status = onboarding.statusFor(Config.hob(), lead);
        List<String> actions = onboardingPage.actions(lead.fullName());

        actual("HOB notifications contain '" + OnboardingFlows.rejectedNotification(lead) + "': "
                + notifications.contains(OnboardingFlows.rejectedNotification(lead)) + ". Status: " + status + ", actions: " + actions);
        Assert.assertTrue(notifications.contains(OnboardingFlows.rejectedNotification(lead)), "Requester was not notified of the LOP rejection");
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Application is not back with the requester");
        Assert.assertTrue(actions.contains("Edit"), "Requester cannot correct the application");
    }
}
