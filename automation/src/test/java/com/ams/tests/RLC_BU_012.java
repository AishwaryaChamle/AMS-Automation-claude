package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_012: Verify upload success/error notification.
 * Expected: System provides an appropriate success or error notification corresponding to the upload outcome.
 */
public class RLC_BU_012 extends BaseTest {

    @Test
    public void uploadShowsSuccessOrErrorNotification() throws Exception {
        Path valid = UploadFiles.bulkLeads(TestData.uniqueFile("bulk_ok", "xlsx"), List.of(TestData.adultLead()));
        String success = flows.bulkUpload(flows.bulkCampaign(), valid);
        Path invalid = UploadFiles.headersOnly("campaign-lead-bulk-upload-template.xlsx", TestData.uniqueFile("bulk_empty", "xlsx"));
        String error = flows.uploadAgain(invalid);

        actual("Valid file: '" + success + "'. Empty file: '" + error + "'");
        Assert.assertTrue(success.contains("File Uploaded Successfully"), "No success notification");
        Assert.assertTrue(error.toLowerCase().contains("error") || error.toLowerCase().contains("failed"), "No error notification");
    }
}
