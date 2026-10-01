package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_007: Verify uploaded blacklist details.
 * Expected: Uploaded blacklist is visible with file name and upload date.
 */
public class RLC_BL_007 extends BaseTest {

    @Test
    public void uploadListingShowsNameDateAndTime() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("blacklist", "xlsx");
        String message = flows.uploadAsAgencyAdmin("Blacklisting", UploadFiles.blacklist(fileName, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic))));
        String row = uploadPage.uploadedFiles().stream().filter(r -> r.startsWith(fileName)).findFirst().orElse("(not listed)");

        actual("Upload: '" + message + "'. Listing row: " + row);
        Assert.assertTrue(row.matches(".*\\| " + TestData.today() + ", \\d{2}:\\d{2}:\\d{2}"), "File name / upload date / time not shown: " + row);
    }
}
