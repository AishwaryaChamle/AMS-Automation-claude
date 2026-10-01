package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_ONB_016: Verify sales code activation for candidate age >= 50
 * Expected: Agency Admin should activate the code and sales code should become active
 */
public class TC_ONB_016 extends BaseTest {

    @Test
    public void salesCodeActivatedForCandidateAged50OrMore() throws Exception {
        Lead lead = onboarding.newLead(TestData.leadAged(52));

        // HOB raises the request and every approver approves it (the Agency Admin approval generates the sales code)
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        String salesCode = onboarding.approveAll(lead, ariTotal);

        // HOB uploads the signed agreement and raises the activation request; Agency Admin activates the code
        String activationRequest = onboarding.uploadAgreementAndRequestActivation(Config.hob(), lead);
        OnboardingFlows.expect(activationRequest, OnboardingFlows.ACTIVATION_RAISED, "Activation request");
        String activation = onboarding.activate(lead);
        String status = onboarding.statusFor(Config.hob(), lead);
        boolean agentCanLogin = loginPage.canLogin(salesCode);

        actual("Candidate aged " + lead.age() + ", ARI " + ariTotal + ". Sales code " + salesCode + " generated. Activation pop-up: '" + activation
                + "'. Status: " + status + ". Agent can log in with the sales code: " + agentCanLogin);
        Assert.assertTrue(activation.contains(OnboardingFlows.SALES_CODE_ACTIVATED), "Sales code was not activated: " + activation);
        Assert.assertEquals(status, OnboardingFlows.STATUS_APPROVED, "Status after activation");
        Assert.assertTrue(agentCanLogin, "Agent with sales code " + salesCode + " cannot log in");
    }
}
