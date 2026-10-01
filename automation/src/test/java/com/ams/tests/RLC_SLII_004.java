package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_004: Verify file upload processing.
 * Expected: File uploads successfully.
 */
public class RLC_SLII_004 extends BaseTest {

    @Test
    public void validCsvFileUploadsSuccessfully() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("slii", "csv");
        String message = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.sliiCsv(fileName, nic, 65));
        String view = uploadPage.viewUploadedFile(fileName);

        actual("CSV upload: '" + message + "'. Details: " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "CSV upload failed: " + message);
        Assert.assertTrue(view.contains(nic), "CSV record not processed");
    }
}
