package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_011: Verify Agreed for Interview status.
 * Expected: Candidate status changes to Agreed for Interview.
 */
public class RLC_HA_011 extends BaseTest {

    @Test
    public void candidateStatusAgreedForInterview() throws Exception {
        // HOB creates a lead with interview status 'Agreed for the interview' and submits it
        Lead lead = TestData.adultLead();
        flows.startLead(Config.hob(), flows.sharedCampaign(), lead);
        leadPage.setInterview(Flows.AGREED, TestData.today(), "Agreed for interview - automation");
        leadPage.clickSubmit();
        String message = leadPage.closeAllPopups();

        // Reopen the lead and read the saved interview status
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());
        String saved = leadPage.selectedInterviewStatus();

        actual("Pop-up: '" + message + "'. Interview status after reopening: " + saved);
        Assert.assertEquals(saved, Flows.AGREED, "Interview status was not saved");
    }
}
