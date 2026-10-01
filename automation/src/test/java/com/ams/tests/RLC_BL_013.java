package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_013: Verify blacklist template acceptance.
 * Expected: Template is accepted for processing.
 */
public class RLC_BL_013 extends BaseTest {

    @Test
    public void validTemplateAcceptedForProcessing() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("blacklist", "xlsx");
        String message = flows.uploadAsAgencyAdmin("Blacklisting", UploadFiles.blacklist(fileName, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic))));

        actual("Upload of " + fileName + ": '" + message + "'");
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "Upload failed: " + message);
    }
}
