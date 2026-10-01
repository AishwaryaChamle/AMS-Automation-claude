package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_009: Verify that the system rejects files with unsupported formats and displays an appropriate error message.
 * Expected: Upload is rejected and an appropriate unsupported-format error is displayed.
 */
public class RLC_BU_009 extends BaseTest {

    @Test
    public void unsupportedFileFormatRejected() throws Exception {
        flows.openBulkUpload(flows.bulkCampaign());

        String message = uploadPage.upload(UploadFiles.textFile(TestData.uniqueFile("bulk_leads", "txt")));

        actual("Upload of a .txt file: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.contains("Unsupported file format"), "Expected error message not shown: " + message);
    }
}
