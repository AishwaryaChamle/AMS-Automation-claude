package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_018: Verify duplicate NIC handling.
 * Expected: Duplicate NIC records are handled according to the system validation behavior and are not incorrectly treated as unique valid leads.
 */
public class RLC_BU_018 extends BaseTest {

    @Test
    public void duplicateNicHandledOnBulkUpload() throws Exception {
        Lead lead = TestData.adultLead();
        Lead sameNic = TestData.withNic(TestData.adultLead(), lead.nic());
        String fileName = TestData.uniqueFile("bulk_duplicate_nic", "xlsx");
        Path file = UploadFiles.bulkLeads(fileName, List.of(lead, sameNic));
        String message = flows.bulkUpload(flows.bulkCampaign(), file);
        String view = message.contains("Successfully") ? uploadPage.viewUploadedFile(fileName) : "(file rejected)";

        actual("Upload with the same NIC twice: '" + message.replace("\n", " | ") + "'. View: " + view.replace("\n", " | "));
        Assert.assertFalse(view.contains("2 total records"), "Both duplicate NIC records were accepted as unique leads");
    }
}
