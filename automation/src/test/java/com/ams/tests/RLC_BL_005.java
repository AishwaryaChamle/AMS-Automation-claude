package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_005: Verify blacklist processing.
 * Expected: Blacklist file uploads successfully.
 */
public class RLC_BL_005 extends BaseTest {

    @Test
    public void validCsvFileUploadsSuccessfully() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("blacklist", "csv");
        String message = flows.uploadAsAgencyAdmin("Blacklisting", UploadFiles.blacklistCsv(fileName, "AUTOMATION CSV", nic));
        String view = uploadPage.viewUploadedFile(fileName);

        actual("CSV upload: '" + message + "'. Details: " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "CSV upload failed: " + message);
        Assert.assertTrue(view.contains(nic), "CSV record not processed");
    }
}
