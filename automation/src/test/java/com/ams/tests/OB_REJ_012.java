package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_012: Verify Agency Admin rejection does not allow the onboarding request to proceed to Agent Agreement Upload.
 * Expected: Agent Agreement Upload stage is not enabled until AA approves the onboarding request.
 */
public class OB_REJ_012 extends BaseTest {

    @Test
    public void agencyAdminRejectionDoesNotEnableAgreementUpload() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approve(Config.lop(), lead);
        onboarding.reject(Config.agencyAdmin(), lead, "Rejected by Agency Admin - automation");

        // HOB opens the rejected application: the agreement upload stage must not be offered
        onboarding.openAgreementStage(Config.hob(), lead);
        boolean agreementOffered = onboardingPage.isAgreementDownloadOffered() || onboardingPage.isAgreementUploadShown();
        String salesCode = onboarding.statusFor(Config.hob(), lead).isEmpty() ? "" : onboardingPage.salesCode(lead.fullName());

        actual("After Agency Admin rejection - agreement upload offered: " + agreementOffered + ", sales code: " + salesCode);
        Assert.assertFalse(agreementOffered, "Agent Agreement upload is offered after Agency Admin rejection");
    }
}
