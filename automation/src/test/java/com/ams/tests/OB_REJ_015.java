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
 * OB_REJ_015: Verify HOB/AS/LOP can upload the Agent Agreement after AA approves the onboarding request and Sales code is genrated.
 * Expected: Agent Agreement Upload stage becomes available only after successful AA approval.
 */
public class OB_REJ_015 extends BaseTest {

    @Test
    public void agreementUploadAvailableOnlyAfterAgencyAdminApproval() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approve(Config.lop(), lead);

        // Before the Agency Admin approval: no sales code and no agreement stage
        String statusBefore = onboarding.statusFor(Config.hob(), lead);
        String salesCodeBefore = onboardingPage.salesCode(lead.fullName());
        boolean editBefore = onboardingPage.actions(lead.fullName()).contains("Edit");

        // After the Agency Admin approval: sales code generated and agreement upload offered
        String salesCode = OnboardingPage.salesCodeIn(onboarding.approve(Config.agencyAdmin(), lead));
        onboarding.openAgreementStage(Config.hob(), lead);
        boolean offeredAfter = onboardingPage.isAgreementDownloadOffered();
        String activationRequest = onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);

        actual("Before AA approval - status " + statusBefore + ", sales code " + salesCodeBefore + ", Edit offered: " + editBefore
                + ". After AA approval - sales code " + salesCode + ", agreement upload offered: " + offeredAfter
                + ", activation request: '" + activationRequest + "'");
        Assert.assertFalse(salesCodeBefore.matches("\\d+"), "Sales code exists before Agency Admin approval");
        Assert.assertFalse(editBefore, "Application can be edited (agreement stage) before Agency Admin approval");
        Assert.assertNotNull(salesCode, "No sales code after Agency Admin approval");
        Assert.assertTrue(offeredAfter, "Agreement upload is not offered after Agency Admin approval");
        Assert.assertTrue(activationRequest.contains(OnboardingFlows.ACTIVATION_RAISED), "Activation request failed: " + activationRequest);
    }
}
