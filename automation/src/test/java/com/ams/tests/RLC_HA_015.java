package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_015: Verify interview scheduling.
 * Expected: Interview is scheduled and the scheduling information is retained.
 */
public class RLC_HA_015 extends BaseTest {

    @Test
    public void interviewSchedulingRetained() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // The Supervisor opens the lead: the HOB interview date must be kept
        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        String hobInterviewDate = leadPage.interviewDateOf("HOB Interview");

        actual("HOB Interview date shown to the Supervisor: " + hobInterviewDate);
        Assert.assertEquals(hobInterviewDate, TestData.today(), "Interview schedule was not retained");
    }
}
