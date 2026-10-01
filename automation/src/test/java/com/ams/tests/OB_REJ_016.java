package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_016: Verify Agent Code Activation request is not available to the AA until a valid Agent Agreement is uploaded and submitted.
 * Expected: System allows activation request only after successful agreement upload.
 */
public class OB_REJ_016 extends BaseTest {

    @Test
    public void activationRequestNotPossibleWithoutAgreementUpload() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approveAll(lead, ariTotal);

        // HOB tries to continue from the witness page without uploading the agreement
        onboarding.openAgreementStage(Config.hob(), lead);
        String withoutAgreement;
        try {
            onboardingPage.next();
            withoutAgreement = onboardingPage.isActivationRequestOffered()
                    ? "activation page reached, pop-up: '" + onboardingPage.raiseActivationRequest() + "'"
                    : "activation page reached without the activation button";
        } catch (AssertionError blocked) {
            withoutAgreement = "blocked: " + blocked.getMessage();
        }
        boolean requestAtAgencyAdmin = onboarding.isPendingWith(Config.agencyAdmin(), lead);

        // With the uploaded agreement the activation request is accepted
        String withAgreement = onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);

        actual("Without agreement: " + withoutAgreement + ". Activation request at Agency Admin before upload: " + requestAtAgencyAdmin
                + ". With agreement: '" + withAgreement + "'");
        Assert.assertFalse(withoutAgreement.contains(OnboardingFlows.ACTIVATION_RAISED), "Activation request raised without an agreement");
        Assert.assertFalse(requestAtAgencyAdmin, "Activation request reached Agency Admin without an agreement");
        Assert.assertTrue(withAgreement.contains(OnboardingFlows.ACTIVATION_RAISED), "Activation request failed after uploading the agreement");
    }
}
