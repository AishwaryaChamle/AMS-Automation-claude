package com.ams.tests;

import com.ams.base.BaseTest;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_008: Verify missing data handling.
 * Expected: Missing data is handled clearly and does not display fabricated or misleading values.
 */
public class RLC_GV_008 extends BaseTest {

    @Test
    public void missingDataNotShownAsMisleadingValues() throws Exception {
        flows.openCampaignListAsAgencyAdmin(flows.sharedCampaign());
        campaignPage.openLeads(flows.sharedCampaign());
        String leadSummary = leadPage.pageText();
        String bad = List.of("null", "undefined", "NaN", "Invalid Date").stream().filter(leadSummary::contains).findFirst().orElse("none");

        actual("Placeholder / fabricated values found in the lead summary: " + bad);
        Assert.assertEquals(bad, "none", "Missing data is shown as '" + bad + "'");
    }
}
