package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_021: Verify repeated AA rejection at Agent Code Activation stage allows repeated agreement correction and resubmission.
 * Expected: Each rejection returns to Agreement Upload, allowing the requester to correct and resubmit until AA approval.
 */
public class OB_REJ_021 extends BaseTest {

    @Test
    public void repeatedActivationRejectionAllowsRepeatedAgreementUpload() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        onboarding.approveAll(lead, ariTotal);
        onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);

        // Two activation rejections, each followed by a new agreement upload; then the Agency Admin activates
        StringBuilder rounds = new StringBuilder();
        for (int round = 1; round <= 2; round++) {
            onboarding.reject(Config.agencyAdmin(), lead, "Activation rejection " + round + " - automation");
            String again = onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);
            rounds.append("Round ").append(round).append(": '").append(again).append("'. ");
            Assert.assertTrue(again.contains(OnboardingFlows.ACTIVATION_RAISED), "Round " + round + ": activation request not raised again");
        }
        String activation = onboarding.activate(lead);

        actual(rounds + "Final Agency Admin approval: '" + activation + "'");
        Assert.assertTrue(activation.contains(OnboardingFlows.SALES_CODE_ACTIVATED), "Sales code was not activated at the end");
    }
}
