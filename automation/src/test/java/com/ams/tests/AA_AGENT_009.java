package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.OnboardingFlows;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_AGENT_009: Verify Agency Admin can select any available branch while creating an agent.
 * Expected: Selected branch is successfully assigned to the newly created agent.
 */
public class AA_AGENT_009 extends BaseTest {

    @Test
    public void agencyAdminSelectsBranchWhenCreatingAgent() throws Exception {
        OnboardingFlows.CreatedAgent agent = onboarding.createAgentAsAgencyAdmin();

        actual("Work Station branch shown: " + agent.branch() + ", branch can be changed: " + agent.branchSelectable()
                + ". Pop-up after SUBMIT: '" + agent.message() + "'");
        Assert.assertNotNull(agent.salesCode(), "Agent was not created: " + agent.message());
        Assert.assertTrue(agent.branchSelectable(), "Agency Admin cannot select a branch: it is fixed to " + agent.branch());
    }
}
