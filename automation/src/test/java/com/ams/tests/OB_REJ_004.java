package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_004: Verify rejection remarks entered by SMBD are available to the original requester after rejection.
 * Expected: HOB/AS/LOP can identify the rejection reason and take corrective action before resubmission.
 */
public class OB_REJ_004 extends BaseTest {

    @Test
    public void srMbdRejectionRemarkVisibleToRequester() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        String remark = "Bank account number is wrong " + System.currentTimeMillis();
        onboarding.reject(OnboardingFlows.smbd(), lead, remark);

        // HOB sees the rejection reason in the list and in the notifications
        String shownRemark = onboarding.statusFor(Config.hob(), lead).equals(OnboardingFlows.STATUS_REJECTED)
                ? onboardingPage.remark(lead.fullName()) : "";
        String notifications = menu.notificationsText();

        actual("Remark entered by Sr MBD: '" + remark + "'. Remark shown to HOB: '" + shownRemark + "'. Rejection notification received: "
                + notifications.contains(OnboardingFlows.rejectedNotification(lead)));
        Assert.assertTrue(shownRemark.contains(remark), "Rejection remark is not shown to the requester");
        Assert.assertTrue(notifications.contains(OnboardingFlows.rejectedNotification(lead)), "Requester was not notified of the rejection");
    }
}
