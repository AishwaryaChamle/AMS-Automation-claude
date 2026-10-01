package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_009: Verify blacklist history/audit.
 * Expected: Blacklist upload history/audit records are displayed.
 */
public class RLC_BL_009 extends BaseTest {

    @Test
    public void uploadedFileDetailsDisplayed() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("blacklist", "xlsx");
        String message = flows.uploadAsAgencyAdmin("Blacklisting", UploadFiles.blacklist(fileName, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic))));
        String view = uploadPage.viewUploadedFile(fileName);

        actual("Upload: '" + message + "'. Details of " + fileName + ": " + view.replace("\n", " | "));
        Assert.assertTrue(view.contains(fileName) && view.contains(nic), "Uploaded file details not displayed");
    }
}
