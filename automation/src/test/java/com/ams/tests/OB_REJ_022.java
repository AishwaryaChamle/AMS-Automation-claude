package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_022: Verify AA approval after a previously rejected Agent Code Activation request activates the Agent Code.
 * Expected: After corrected agreement submission and AA approval, Agent Code is successfully activated.
 */
public class OB_REJ_022 extends BaseTest {

    @Test
    public void activationApprovedAfterEarlierRejectionActivatesAgentCode() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        String salesCode = onboarding.approveAll(lead, ariTotal);
        onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);

        onboarding.reject(Config.agencyAdmin(), lead, "Please upload the signed agreement - automation");
        onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);
        String activation = onboarding.activate(lead);
        String status = onboarding.statusFor(Config.hob(), lead);
        boolean agentCanLogin = loginPage.canLogin(salesCode);

        actual("After rejection and corrected agreement: '" + activation + "'. Status " + status + ". Agent " + salesCode + " can log in: " + agentCanLogin);
        Assert.assertTrue(activation.contains(OnboardingFlows.SALES_CODE_ACTIVATED), "Sales code was not activated");
        Assert.assertEquals(status, OnboardingFlows.STATUS_APPROVED, "Status after activation");
        Assert.assertTrue(agentCanLogin, "Activated agent cannot log in");
    }
}
