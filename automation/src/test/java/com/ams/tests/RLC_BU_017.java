package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_017: Verify upload failure handling.
 * Expected: Upload fails and an appropriate error message is displayed.
 */
public class RLC_BU_017 extends BaseTest {

    @Test
    public void uploadFailureShowsErrorMessage() throws Exception {
        flows.openBulkUpload(flows.bulkCampaign());

        String message = uploadPage.upload(UploadFiles.corruptedExcel(TestData.uniqueFile("bulk_corrupted", "xlsx")));

        actual("Upload of a corrupted file: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.toLowerCase().matches("(?s).*(failed|error|missing|invalid).*"), "Expected error message not shown: " + message);
    }
}
