package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_LM_006: Verify new lead status.
 * Expected: New lead status is saved and displayed.
 */
public class RLC_LM_006 extends BaseTest {

    @Test
    public void newLeadStatusSaved() throws Exception {
        // HOB creates a lead with interview status 'Not available for the interview' and submits it
        Lead lead = TestData.adultLead();
        flows.startLead(Config.hob(), flows.sharedCampaign(), lead);
        leadPage.setInterview(Flows.NOT_AVAILABLE, null, null);
        leadPage.clickSubmit();
        String message = leadPage.closeAllPopups();

        // Reopen the lead and read the saved interview status
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());
        String saved = leadPage.selectedInterviewStatus();

        actual("Pop-up: '" + message + "'. Interview status after reopening: " + saved);
        Assert.assertEquals(saved, Flows.NOT_AVAILABLE, "Interview status was not saved");
    }
}
