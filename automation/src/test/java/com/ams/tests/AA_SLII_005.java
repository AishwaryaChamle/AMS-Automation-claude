package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_SLII_005: Verify Agency Admin can upload a valid SLII Exam Bulk Excel file.
 * Expected: SLII exam data is uploaded successfully and becomes available for use during agent onboarding.
 */
public class AA_SLII_005 extends BaseTest {

    @Test
    public void agencyAdminUploadsValidSliiFile() throws Exception {
        // Agency Admin uploads a valid SLII exam result file (.xlsx)
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("slii_results", "xlsx");
        Path file = UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 72)));
        String message = flows.uploadAsAgencyAdmin("SLII Exam Management", file);

        // The file is listed and its details contain the uploaded NIC
        String view = uploadPage.viewUploadedFile(fileName);

        actual("Upload message: '" + message + "'. Details of " + fileName + ": " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "Upload was not successful: " + message);
        Assert.assertTrue(view.contains(nic), "Uploaded SLII record is not shown");
    }
}
