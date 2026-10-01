package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_018: Verify required documentation availability.
 * Expected: Candidate can proceed when the applicable required supporting documentation is present.
 */
public class RLC_HA_018 extends BaseTest {

    @Test
    public void youngCandidateProceedsWithRequiredDocument() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        boolean movedOn = approvalTray.isListed(lead.fullName());

        actual("Age " + lead.age() + " with document: lead moved to Supervisor: " + movedOn);
        Assert.assertTrue(movedOn, "Candidate with documents could not proceed");
    }
}
