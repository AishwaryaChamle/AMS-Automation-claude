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
 * OB_REJ_017: Verify AA rejection at the Agent Code Activation stage returns the request specifically to the Agent Agreement Upload stage.
 * Expected: Request does not return to SMBD/LOP/AA onboarding approval; it returns directly to Agent Agreement Upload.
 */
public class OB_REJ_017 extends BaseTest {

    @Test
    public void activationRejectionReturnsToAgreementUploadStage() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approveAll(lead, ariTotal);
        onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);

        // Agency Admin rejects the activation request
        onboarding.reject(Config.agencyAdmin(), lead, "Agreement not signed - automation");
        boolean withLop = onboarding.isPendingWith(Config.lop(), lead);
        boolean withSrMbd = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);

        // HOB opens it again: it is back at the agreement upload with the activation request
        String status = onboarding.statusFor(Config.hob(), lead);
        onboarding.openAgreementStage(Config.hob(), lead);
        boolean agreementUpload = onboardingPage.isAgreementUploadShown() || onboardingPage.isAgreementDownloadOffered();
        onboardingPage.nextUntil(OnboardingPage.ARI_STEP);
        boolean activationOffered = onboardingPage.isActivationRequestOffered();
        boolean onboardingRequestOffered = onboardingPage.isRaiseRequestOffered();

        actual("After activation rejection - status " + status + ", with LOP: " + withLop + ", with Sr MBD: " + withSrMbd
                + ", agreement upload shown: " + agreementUpload + ", activation request offered: " + activationOffered
                + ", onboarding Raise Request offered: " + onboardingRequestOffered);
        Assert.assertFalse(withLop, "Rejected activation went back to LOP");
        Assert.assertFalse(withSrMbd, "Rejected activation went back to Sr MBD");
        Assert.assertTrue(agreementUpload, "Agreement upload stage is not available again");
        Assert.assertTrue(activationOffered, "Activation request is not offered again");
        Assert.assertFalse(onboardingRequestOffered, "Request went back to onboarding approval instead of agreement upload");
    }
}
