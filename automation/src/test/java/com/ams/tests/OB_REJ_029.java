package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_029: Verify rejection remarks are retained for each rejection cycle.
 * Expected: Each rejection contains the applicable approver's remarks, allowing the requester to understand the reason for rejection.
 */
public class OB_REJ_029 extends BaseTest {

    @Test
    public void rejectionRemarksKeptForEachRejectionCycle() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        // Cycle 1: Sr MBD rejects; cycle 2 (after resubmission): Sr MBD rejects with a different remark
        String firstRemark = "First rejection: wrong bank account " + System.currentTimeMillis();
        onboarding.reject(OnboardingFlows.smbd(), lead, firstRemark);
        onboarding.statusFor(Config.hob(), lead);
        String firstShown = onboardingPage.remark(lead.fullName());
        onboarding.resubmit(Config.hob(), lead);

        String secondRemark = "Second rejection: agreement details missing " + System.currentTimeMillis();
        onboarding.reject(OnboardingFlows.smbd(), lead, secondRemark);
        onboarding.statusFor(Config.hob(), lead);
        String secondShown = onboardingPage.remark(lead.fullName());
        int rejectionNotifications = OnboardingFlows.occurrences(menu.notificationsText(), OnboardingFlows.rejectedNotification(lead));

        actual("Cycle 1 remark shown: '" + firstShown + "'. Cycle 2 remark shown: '" + secondShown
                + "'. Rejection notifications received: " + rejectionNotifications);
        Assert.assertTrue(firstShown.contains(firstRemark), "First rejection remark was not shown");
        Assert.assertTrue(secondShown.contains(secondRemark), "Second rejection remark was not shown");
        Assert.assertEquals(rejectionNotifications, 2, "Expected one rejection notification per cycle");
    }
}
