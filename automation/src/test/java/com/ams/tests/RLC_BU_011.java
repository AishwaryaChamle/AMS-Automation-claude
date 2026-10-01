package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_011: Verify successful file processing and uploaded count.
 * Expected: File is processed successfully and the uploaded count reflects successfully uploaded records.
 */
public class RLC_BU_011 extends BaseTest {

    @Test
    public void uploadedCountReflectsSuccessfulRecords() throws Exception {
        String fileName = TestData.uniqueFile("bulk_three_leads", "xlsx");
        Path file = UploadFiles.bulkLeads(fileName, List.of(TestData.adultLead(), TestData.adultLead(), TestData.adultLead()));
        String message = flows.bulkUpload(flows.bulkCampaign(), file);
        String view = uploadPage.viewUploadedFile(fileName);

        actual("Upload: '" + message + "'. View: " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "Upload failed: " + message);
        Assert.assertTrue(view.contains("3 total records"), "Uploaded count is not 3");
    }
}
