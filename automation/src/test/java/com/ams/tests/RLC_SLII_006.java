package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_006: Verify upload listing.
 * Expected: Listing displays the file name, upload date, and upload time.
 */
public class RLC_SLII_006 extends BaseTest {

    @Test
    public void uploadListingShowsNameDateAndTime() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("slii", "xlsx");
        String message = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        String row = uploadPage.uploadedFiles().stream().filter(r -> r.startsWith(fileName)).findFirst().orElse("(not listed)");

        actual("Upload: '" + message + "'. Listing row: " + row);
        Assert.assertTrue(row.matches(".*\\| " + TestData.today() + ", \\d{2}:\\d{2}:\\d{2}"), "File name / upload date / time not shown: " + row);
    }
}
