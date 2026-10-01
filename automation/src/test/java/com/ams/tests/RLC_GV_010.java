package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_010: Verify upload failure reporting.
 * Expected: Failure is clearly indicated with an appropriate error/validation message and is not reported as a successful upload.
 */
public class RLC_GV_010 extends BaseTest {

    @Test
    public void uploadFailureReportedClearly() throws Exception {
        // Invalid termination date makes the upload fail
        Object[] row = UploadFiles.blacklistRow("FAILED UPLOAD", TestData.adultLead().nic());
        row[4] = "31-31-2026";
        String fileName = TestData.uniqueFile("blacklist_invalid_date", "xlsx");
        String message = flows.uploadAsAgencyAdmin("Blacklisting", UploadFiles.blacklist(fileName, List.<Object[]>of(row)));
        boolean listed = uploadPage.indexOfFile(fileName) >= 0;

        actual("Upload message: '" + message.replace("\n", " | ") + "'. Failed file listed as uploaded: " + listed);
        Assert.assertFalse(message.contains("Successfully"), "Invalid file was reported as successful");
        Assert.assertFalse(listed, "Failed file appears in the uploaded list");
    }
}
