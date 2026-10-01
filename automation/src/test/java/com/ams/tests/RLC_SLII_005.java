package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_005: Verify repeated file upload.
 * Expected: File uploads successfully.
 */
public class RLC_SLII_005 extends BaseTest {

    @Test
    public void repeatedUploadSucceeds() throws Exception {
        // First upload
        String nic = TestData.adultLead().nic();
        String firstFile = TestData.uniqueFile("slii_first", "xlsx");
        String first = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(firstFile, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        // Second upload (a different candidate: the same records are refused as already present)
        nic = TestData.adultLead().nic();
        String secondFile = TestData.uniqueFile("slii_second", "xlsx");
        String second = flows.uploadAgain(UploadFiles.slii(secondFile, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        boolean bothListed = uploadPage.indexOfFile(firstFile) >= 0 && uploadPage.indexOfFile(secondFile) >= 0;

        actual("First: '" + first + "', second: '" + second + "'. Both listed: " + bothListed);
        Assert.assertTrue(first.contains("Successfully") && second.contains("Successfully"), "Repeated upload failed");
    }
}
