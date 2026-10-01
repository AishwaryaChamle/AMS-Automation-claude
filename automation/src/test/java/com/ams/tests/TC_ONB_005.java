package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_005: Verify document upload accessibility after Agency Admin approval for ARI < 30
 * Expected: HOB, AS and LOP should be able to upload document and raise activation request
 */
public class TC_ONB_005 extends BaseTest {

    @Test
    public void hobAsLopCanUploadAgreementAfterApprovalForAriBelow30() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);
        String salesCode = onboarding.approveAll(lead, ariTotal);

        // After the Agency Admin approval HOB, AS and LOP open the application: the agreement upload must be offered
        onboarding.openAgreementStage(Config.hob(), lead);
        boolean hob = onboardingPage.isAgreementDownloadOffered();
        onboarding.openAgreementStage(Config.agencySupervisor(), lead);
        boolean as = onboardingPage.isAgreementDownloadOffered();
        onboarding.openAgreementStage(Config.lop(), lead);
        boolean lop = onboardingPage.isAgreementDownloadOffered();

        // HOB uploads the agreement and raises the activation request
        String activationRequest = onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);

        actual("ARI " + ariTotal + ", sales code " + salesCode + ". Agreement download/upload offered - HOB: " + hob + ", AS: " + as
                + ", LOP: " + lop + ". Activation request pop-up: '" + activationRequest + "'");
        Assert.assertTrue(hob, "HOB cannot upload the agreement");
        Assert.assertTrue(as, "AS cannot upload the agreement");
        Assert.assertTrue(lop, "LOP cannot upload the agreement");
        Assert.assertTrue(activationRequest.contains(OnboardingFlows.ACTIVATION_RAISED), "Activation request was not raised: " + activationRequest);
    }
}
