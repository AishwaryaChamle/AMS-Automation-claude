package com.ams.tests;

import com.ams.base.BaseTest;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_001: Verify that the Bulk Lead Upload functionality is available to the authorized user.
 * Expected: Bulk lead upload functionality is available.
 */
public class RLC_BU_001 extends BaseTest {

    @Test
    public void bulkLeadUploadAvailableToAuthorizedUser() throws Exception {
        flows.openCampaignListAsAgencyAdmin(flows.bulkCampaign());
        List<String> actions = campaignPage.actionsOf(flows.bulkCampaign());
        flows.openBulkUpload(flows.bulkCampaign());
        String instruction = uploadPage.uploadInstruction();

        actual("Campaign actions: " + actions + ". Bulk upload page instruction: " + instruction);
        Assert.assertTrue(actions.contains("Bulk Upload"), "Bulk Upload action missing");
        Assert.assertTrue(uploadPage.isUploaderShown(), "Bulk upload screen is not available");
    }
}
