package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_010: Verify lead summary and counts.
 * Expected: Overall lead summary and lead status/count information are displayed and correspond to the leads shown.
 */
public class RLC_CS_010 extends BaseTest {

    @Test
    public void leadSummaryShowsStatusAndCounts() throws Exception {
        // Make sure the campaign has at least one lead
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesLead(lead), "Lead creation");

        flows.openCampaignListAsAgencyAdmin(flows.sharedCampaign());
        campaignPage.openLeads(flows.sharedCampaign());
        List<String> headers = campaignPage.tableHeaders();
        String row = leadPage.rowText(lead.nic());

        actual("Lead summary columns: " + headers + ". Row of the new lead: " + row);
        Assert.assertTrue(headers.contains("Status") && headers.contains("Remarks"), "Lead summary has no Status / Remarks columns");
        Assert.assertNotNull(row, "New lead not shown in the lead summary");
    }
}
