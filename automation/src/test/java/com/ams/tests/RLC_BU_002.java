package com.ams.tests;

import com.ams.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_002: Verify individual lead entry is unavailable when bulk upload is mandatory. Test Objective: Verify that users cannot enter or create leads individually when the requirement restricts lead creation to Bulk Upload only.
 * Expected: Individual lead entry is unavailable where the requirement restricts entry to bulk upload.
 */
public class RLC_BU_002 extends BaseTest {

    @Test
    public void individualLeadEntryUnavailableForBulkOnlyUser() throws Exception {
        // Agency Admin can add leads only by bulk upload: no Create Lead on the campaign's lead list
        flows.openCampaignListAsAgencyAdmin(flows.bulkCampaign());
        campaignPage.openLeads(flows.bulkCampaign());
        boolean createLeadShown = leadPage.isCreateLeadAvailable();

        actual("Create Lead option shown to Agency Admin: " + createLeadShown);
        Assert.assertFalse(createLeadShown, "Individual lead entry is available to Agency Admin");
    }
}
