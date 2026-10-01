package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_016: Verify empty file handling.
 * Expected: Upload is rejected and an appropriate empty-file/error message is displayed.
 */
public class RLC_BU_016 extends BaseTest {

    @Test
    public void emptyFileRejected() throws Exception {
        flows.openBulkUpload(flows.bulkCampaign());

        String message = uploadPage.upload(UploadFiles.headersOnly("campaign-lead-bulk-upload-template.xlsx", TestData.uniqueFile("bulk_empty", "xlsx")));

        actual("Upload of a file without data rows: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        // The application rejects a file without data rows with the "Upload Failed" message
        Assert.assertTrue(message.contains("Upload Failed") && message.contains("could not be uploaded"),
                "Expected error message not shown: " + message);
    }
}
