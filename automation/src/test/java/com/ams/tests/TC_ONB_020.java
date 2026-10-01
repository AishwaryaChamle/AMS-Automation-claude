package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.pages.OnboardingPage;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_020: Verify rejected onboarding case status
 * Expected: Rejected case should not allow activation request and status should be displayed as "Rejected"
 */
public class TC_ONB_020 extends BaseTest {

    @Test
    public void rejectedCaseShowsRejectedStatusWithoutActivationRequest() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");

        // HOB opens the rejected case: no agreement upload and no activation request should be offered
        String status = onboarding.statusFor(Config.hob(), lead);
        onboardingPage.edit(lead.fullName());
        onboardingPage.nextUntil(OnboardingPage.WITNESS_STEP);
        boolean agreementOffered = onboardingPage.isAgreementDownloadOffered();
        onboardingPage.nextUntil(OnboardingPage.ARI_STEP);
        boolean activationOffered = onboardingPage.isActivationRequestOffered();

        actual("Status: " + status + ". Agreement upload offered: " + agreementOffered + ", activation request offered: " + activationOffered);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Status of the rejected case");
        Assert.assertFalse(agreementOffered, "Agreement upload is offered for a rejected case");
        Assert.assertFalse(activationOffered, "Activation request is offered for a rejected case");
    }
}
