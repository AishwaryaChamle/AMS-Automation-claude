package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_010: Verify candidate approval.
 * Expected: Approval is accepted and candidate status/workflow is updated.
 */
public class RLC_HA_010 extends BaseTest {

    @Test
    public void hobApprovesCandidate() throws Exception {
        Lead lead = TestData.adultLead();
        String message = flows.hobCreatesAndApprovesLead(lead);
        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        boolean movedOn = approvalTray.isListed(lead.fullName());

        actual("HOB pop-ups: '" + message + "'. Lead moved to Supervisor: " + movedOn);
        Assert.assertTrue(message.contains("Lead Approved Successfully"), "Approval not accepted: " + message);
        Assert.assertTrue(movedOn, "Workflow was not updated after approval");
    }
}
