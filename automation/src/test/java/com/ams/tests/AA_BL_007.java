package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_BL_007: Verify Agency Admin can upload a valid Blacklisted Agents Excel file.
 * Expected: Blacklisted agent records are uploaded successfully and become available for blacklist validation.
 */
public class AA_BL_007 extends BaseTest {

    @Test
    public void agencyAdminUploadsValidBlacklistFile() throws Exception {
        // Agency Admin uploads a valid blacklisting file (.xlsx)
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("blacklist", "xlsx");
        Path file = UploadFiles.blacklist(fileName, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic)));
        String message = flows.uploadAsAgencyAdmin("Blacklisting", file);

        String view = uploadPage.viewUploadedFile(fileName);

        actual("Upload message: '" + message + "'. Details of " + fileName + ": " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "Upload was not successful: " + message);
        Assert.assertTrue(view.contains(nic), "Uploaded blacklist record is not shown");
    }
}
