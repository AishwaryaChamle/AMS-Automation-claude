package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_007: Verify uploaded file details.
 * Expected: Uploaded file details are displayed.
 */
public class RLC_SLII_007 extends BaseTest {

    @Test
    public void uploadedFileDetailsDisplayed() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("slii", "xlsx");
        String message = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        String view = uploadPage.viewUploadedFile(fileName);

        actual("Upload: '" + message + "'. Details of " + fileName + ": " + view.replace("\n", " | "));
        Assert.assertTrue(view.contains(fileName) && view.contains(nic), "Uploaded file details not displayed");
    }
}
