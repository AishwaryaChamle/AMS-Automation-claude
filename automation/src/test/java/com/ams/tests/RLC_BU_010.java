package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_010: Verify that invalid records in the uploaded file are identified or rejected and appropriate validation information is displayed.
 * Expected: Invalid records are identified/rejected and appropriate validation information is displayed.
 */
public class RLC_BU_010 extends BaseTest {

    @Test
    public void invalidRecordsIdentifiedWithValidationInfo() throws Exception {
        flows.openBulkUpload(flows.bulkCampaign());

        // One valid row and one row with an invalid NIC
        Object[] invalid = UploadFiles.bulkLeadRow(TestData.adultLead());
        invalid[0] = "ABC123";
        Path file = UploadFiles.bulkLeadRows(TestData.uniqueFile("bulk_invalid_nic", "xlsx"),
                List.of(UploadFiles.bulkLeadRow(TestData.adultLead()), invalid));
        String message = uploadPage.upload(file);

        actual("Upload with an invalid NIC row: '" + message.replace("\n", " | ") + "'");
        Assert.assertTrue(message.contains("NIC"), "Invalid NIC record was not identified: " + message);
    }
}
