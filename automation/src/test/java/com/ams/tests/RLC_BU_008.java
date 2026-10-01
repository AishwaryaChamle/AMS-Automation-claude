package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_008: Verify that a CSV file is accepted when CSV is a supported upload format and rejected with an appropriate supported-format message when CSV is not supported.
 * Expected: CSV is accepted if supported by the final implemented requirement; otherwise the system rejects it with a supported-format message.
 */
public class RLC_BU_008 extends BaseTest {

    @Test
    public void csvBulkLeadFileAccepted() throws Exception {
        Lead lead = TestData.adultLead();
        String fileName = TestData.uniqueFile("bulk_leads", "csv");
        String message = flows.bulkUpload(flows.bulkCampaign(), UploadFiles.bulkLeadsCsv(fileName, lead));
        String view = uploadPage.viewUploadedFile(fileName);

        actual("CSV upload: '" + message + "'. View: " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "CSV file was not accepted: " + message);
        Assert.assertTrue(view.contains(lead.nic()), "CSV lead not shown");
    }
}
