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
 * AA_LEAD_004: Verify Agency Admin can upload a valid bulk lead Excel file against an active campaign.
 * Expected: Bulk lead file is uploaded successfully and valid leads are associated with the selected campaign.
 */
public class AA_LEAD_004 extends BaseTest {

    @Test
    public void agencyAdminUploadsValidBulkLeadFile() throws Exception {
        // Agency Admin uploads a valid bulk-lead file for an ongoing campaign
        Lead first = TestData.adultLead();
        Lead second = TestData.adultLead();
        String fileName = TestData.uniqueFile("bulk_leads", "xlsx");
        Path file = UploadFiles.bulkLeads(fileName, List.of(first, second));
        String message = flows.bulkUpload(flows.bulkCampaign(), file);

        // The file must be listed and its View list must contain the uploaded leads
        String view = uploadPage.viewUploadedFile(fileName);

        actual("Upload message: '" + message + "'. View list of " + fileName + ": " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "Upload was not successful: " + message);
        Assert.assertTrue(view.contains(first.nic()) && view.contains(second.nic()), "Uploaded leads are not shown in the file's lead list");
    }
}
