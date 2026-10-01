package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.OnboardingFlows;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_AGENT_010: Verify sales code is activated automatically after successful agent creation by Agency Admin.
 * Expected: Sales code is activated directly without additional approval, and the newly created agent can log in successfully.
 */
public class AA_AGENT_010 extends BaseTest {

    @Test
    public void salesCodeActivatedDirectlyAndAgentCanLogIn() throws Exception {
        OnboardingFlows.CreatedAgent agent = onboarding.createAgentAsAgencyAdmin();
        boolean agentCanLogin = agent.salesCode() != null && loginPage.canLogin(agent.salesCode());

        actual("Pop-up after SUBMIT: '" + agent.message() + "'. Agent " + agent.salesCode() + " can log in: " + agentCanLogin);
        Assert.assertTrue(agent.message().contains(OnboardingFlows.SALES_CODE_ACTIVATED), "Sales code was not activated directly: " + agent.message());
        Assert.assertTrue(agentCanLogin, "New agent cannot log in");
    }
}
