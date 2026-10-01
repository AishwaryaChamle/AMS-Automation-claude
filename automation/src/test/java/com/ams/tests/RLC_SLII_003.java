package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_003: Verify file upload.
 * Expected: File uploads successfully.
 */
public class RLC_SLII_003 extends BaseTest {

    @Test
    public void validFileUploadsSuccessfully() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("slii", "xlsx");
        String message = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));

        actual("Upload of " + fileName + ": '" + message + "'");
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "Upload failed: " + message);
    }
}
