package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_006: Verify sales code activation for ARI < 30 cases
 * Expected: Agency Admin should activate the code and sales code should become active
 */
public class TC_ONB_006 extends BaseTest {

    @Test
    public void salesCodeActivatedForAriBelow30() throws Exception {
        Lead lead = onboarding.newLead();

        // HOB raises the request (ARI < 30): Sr MBD, LOP and Agency Admin approve
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);
        String salesCode = onboarding.approveAll(lead, ariTotal);

        // HOB uploads the signed agreement and raises the activation request
        String activationRequest = onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);
        OnboardingFlows.expect(activationRequest, OnboardingFlows.ACTIVATION_RAISED, "Activation request");

        // Agency Admin activates the sales code
        String activation = onboarding.activate(lead);
        String status = onboarding.statusFor(Config.hob(), lead);
        boolean agentCanLogin = loginPage.canLogin(salesCode);

        actual("ARI " + ariTotal + ". Sales code " + salesCode + " generated. Activation pop-up: '" + activation
                + "'. Status: " + status + ". Agent can log in with the sales code: " + agentCanLogin);
        Assert.assertTrue(activation.contains(OnboardingFlows.SALES_CODE_ACTIVATED), "Sales code was not activated: " + activation);
        Assert.assertEquals(status, OnboardingFlows.STATUS_APPROVED, "Status after activation");
        Assert.assertTrue(agentCanLogin, "Agent with sales code " + salesCode + " cannot log in");
    }
}
