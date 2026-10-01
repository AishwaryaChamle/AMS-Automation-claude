package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_019: Verify invalid template/structure handling.
 * Expected: File is rejected and an appropriate template/structure validation error is displayed.
 */
public class RLC_BU_019 extends BaseTest {

    @Test
    public void invalidTemplateRejected() throws Exception {
        flows.openBulkUpload(flows.bulkCampaign());

        String message = uploadPage.upload(UploadFiles.wrongTemplate(TestData.uniqueFile("bulk_wrong_template", "xlsx")));

        actual("Upload of a file with a wrong template: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.toLowerCase().matches("(?s).*(failed|error|missing|invalid).*"), "Expected error message not shown: " + message);
    }
}
