package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_016: Verify workflow for candidates above 22.
 * Expected: Candidate follows the workflow applicable to candidates above 22.
 */
public class RLC_HA_016 extends BaseTest {

    @Test
    public void adultCandidateFollowsHobAndSupervisorWorkflow() throws Exception {
        Lead lead = TestData.adultLead();
        flows.fullyApproveAdultLead(lead);

        // No Agency Admin approval is needed for candidates above 22
        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        boolean sentToAdmin = approvalTray.isListed(lead.fullName());
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());

        actual("Age " + lead.age() + ": after HOB + Supervisor approval - sent to Agency Admin: " + sentToAdmin + ", Proceed enabled: " + proceedEnabled);
        Assert.assertFalse(sentToAdmin, "Adult candidate was sent to Agency Admin");
        Assert.assertTrue(proceedEnabled, "Adult candidate cannot proceed after Supervisor approval");
    }
}
