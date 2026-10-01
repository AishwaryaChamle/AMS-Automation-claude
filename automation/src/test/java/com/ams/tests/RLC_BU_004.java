package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_004: Verify that files containing all required columns are accepted for structural validation, while files missing any required column are rejected with an appropriate validation message.
 * Expected: Files containing all required columns are accepted for structural validation; files missing a required column are rejected with an appropriate validation message.
 */
public class RLC_BU_004 extends BaseTest {

    @Test
    public void requiredColumnsValidatedOnBulkUpload() throws Exception {
        // File with all required columns
        Path valid = UploadFiles.bulkLeads(TestData.uniqueFile("bulk_all_columns", "xlsx"), List.of(TestData.adultLead()));
        String validMessage = flows.bulkUpload(flows.bulkCampaign(), valid);

        // File without the NIC column
        Path missing = UploadFiles.bulkLeadsWithoutColumn(TestData.uniqueFile("bulk_no_nic", "xlsx"), TestData.adultLead(), "NIC");
        String missingMessage = flows.uploadAgain(missing);

        actual("All columns: '" + validMessage + "'. Missing NIC column: '" + missingMessage + "'");
        Assert.assertTrue(validMessage.contains("File Uploaded Successfully"), "File with all columns was not accepted");
        // The message wording has changed over releases ("Missing required columns: nic" / "mandatory columns are not filled: NIC")
        String missingText = missingMessage.toLowerCase();
        Assert.assertTrue(missingText.contains("nic") && (missingText.contains("missing") || missingText.contains("mandatory")),
                "Missing NIC column was not reported");
    }
}
