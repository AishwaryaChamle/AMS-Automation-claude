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
 * OB_REJ_026: Verify Agency Admin rejection for an ARI > 30 request returns it to the original HOB/AS/LOP requester.
 * Expected: Request returns to the original requester and does not proceed to Agent Agreement Upload.
 */
public class OB_REJ_026 extends BaseTest {

    @Test
    public void agencyAdminRejectionForAri30OrMoreReturnsToRequester() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approve(Config.lop(), lead);

        onboarding.reject(Config.agencyAdmin(), lead, "Rejected by Agency Admin - automation");
        String status = onboarding.statusFor(Config.hob(), lead);
        onboardingPage.edit(lead.fullName());
        onboardingPage.nextUntil(OnboardingPage.WITNESS_STEP);
        boolean agreementOffered = onboardingPage.isAgreementDownloadOffered() || onboardingPage.isAgreementUploadShown();

        actual("After Agency Admin rejection - status for HOB: " + status + ", agreement upload offered: " + agreementOffered);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Request did not return to HOB");
        Assert.assertFalse(agreementOffered, "Request moved on to the Agent Agreement upload");
    }
}
