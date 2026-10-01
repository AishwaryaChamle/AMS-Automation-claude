package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_AGENT_008: Verify Agency Admin can create an agent by entering valid agent details.
 * Expected: Agent is created successfully with a unique agent/sales code.
 */
public class AA_AGENT_008 extends BaseTest {

    @Test
    public void agencyAdminCreatesAgentWithUniqueSalesCode() throws Exception {
        // Agency Admin: New Application > NIC > applicant, documents, contact, work station, bank > SUBMIT
        OnboardingFlows.CreatedAgent agent = onboarding.createAgentAsAgencyAdmin();

        // The new agent is listed with the sales code
        onboarding.openInSummary(Config.agencyAdmin(), agent.person());
        String row = onboardingPage.rowText(agent.person().fullName());

        actual("Pop-up after SUBMIT: '" + agent.message() + "'. Summary row: " + row);
        Assert.assertNotNull(agent.salesCode(), "No sales code was shown: " + agent.message());
        Assert.assertTrue(row != null && row.contains(agent.salesCode()), "Agent is not listed with sales code " + agent.salesCode());
    }
}
