package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_014: Verify Cannot Be Contacted status.
 * Expected: Candidate status changes to Cannot Be Contacted.
 */
public class RLC_HA_014 extends BaseTest {

    @Test
    public void candidateStatusCannotBeContacted() throws Exception {
        // HOB creates a lead with interview status 'Cannot be contacted' and submits it
        Lead lead = TestData.adultLead();
        flows.startLead(Config.hob(), flows.sharedCampaign(), lead);
        leadPage.setInterview(Flows.CANNOT_BE_CONTACTED, null, null);
        leadPage.clickSubmit();
        String message = leadPage.closeAllPopups();

        // Reopen the lead and read the saved interview status
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());
        String saved = leadPage.selectedInterviewStatus();

        actual("Pop-up: '" + message + "'. Interview status after reopening: " + saved);
        Assert.assertEquals(saved, Flows.CANNOT_BE_CONTACTED, "Interview status was not saved");
    }
}
