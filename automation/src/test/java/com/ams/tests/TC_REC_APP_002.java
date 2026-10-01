package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_002: Verify TRSM (Immediate Supervisor) interview approval for candidate age >= 22
 * Expected: TRSM (Immediate Supervisor) should be able to approve the interview successfully
 */
public class TC_REC_APP_002 extends BaseTest {

    @Test
    public void supervisorApprovesInterviewForCandidateAged22OrAbove() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor (TRSM) approves the lead from the Approval Tray
        String message = flows.approveInTray(Config.supervisor(), lead);

        actual("Supervisor approval pop-up: '" + message + "' for " + lead.fullName() + " (age " + lead.age() + ")");
        Assert.assertTrue(message.contains("Lead Approved Successfully"), "Supervisor approval failed: " + message);
    }
}
