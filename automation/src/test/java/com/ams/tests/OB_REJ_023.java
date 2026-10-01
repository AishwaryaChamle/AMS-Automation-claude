package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_023: Verify the agent cannot log in when AA rejects the final Agent Code Activation request.
 * Expected: Agent Code remains inactive and the agent cannot log in until AA provides final approval.
 */
public class OB_REJ_023 extends BaseTest {

    @Test
    public void agentCannotLogInWhenActivationIsRejected() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);
        String salesCode = onboarding.approveAll(lead, ariTotal);
        onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);

        onboarding.reject(Config.agencyAdmin(), lead, "Activation rejected - automation");
        String status = onboarding.statusFor(Config.hob(), lead);
        boolean agentCanLogin = loginPage.canLogin(salesCode);

        actual("Activation rejected. Status " + status + ". Agent " + salesCode + " can log in: " + agentCanLogin);
        Assert.assertFalse(agentCanLogin, "Agent can log in although the activation was rejected");
        Assert.assertNotEquals(status, OnboardingFlows.STATUS_APPROVED, "Application shows Approved although activation was rejected");
    }
}
