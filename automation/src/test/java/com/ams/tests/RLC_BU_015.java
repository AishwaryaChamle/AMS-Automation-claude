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
 * RLC_BU_015: Verify invalid records handling.
 * Expected: Invalid records are identified as errors and are not counted as successfully uploaded records.
 */
public class RLC_BU_015 extends BaseTest {

    @Test
    public void invalidRecordsNotCountedAsUploaded() throws Exception {
        Lead validLead = TestData.adultLead();
        Object[] invalid = UploadFiles.bulkLeadRow(TestData.adultLead());
        invalid[0] = "12345";
        String fileName = TestData.uniqueFile("bulk_mixed", "xlsx");
        Path file = UploadFiles.bulkLeadRows(fileName, List.of(UploadFiles.bulkLeadRow(validLead), invalid));
        String message = flows.bulkUpload(flows.bulkCampaign(), file);

        // If the file was accepted, only the valid record may be counted
        String view = message.contains("Successfully") ? uploadPage.viewUploadedFile(fileName) : "(file rejected, nothing uploaded)";

        actual("Upload message: '" + message.replace("\n", " | ") + "'. View: " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("NIC") || view.contains("1 total records"), "Invalid record was not identified as an error");
        Assert.assertFalse(view.contains("2 total records"), "Invalid record was counted as uploaded");
    }
}
