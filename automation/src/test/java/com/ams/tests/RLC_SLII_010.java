package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_010: Verify monthly upload history.
 * Expected: All valid uploads within the month are retained in history.
 */
public class RLC_SLII_010 extends BaseTest {

    @Test
    public void allUploadsWithinMonthRetained() throws Exception {
        // First upload
        String nic = TestData.adultLead().nic();
        String firstFile = TestData.uniqueFile("slii_first", "xlsx");
        String first = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(firstFile, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        // Second upload (a different candidate: the same records are refused as already present)
        nic = TestData.adultLead().nic();
        String secondFile = TestData.uniqueFile("slii_second", "xlsx");
        String second = flows.uploadAgain(UploadFiles.slii(secondFile, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        List<String> files = uploadPage.uploadedFiles();
        boolean bothListed = uploadPage.indexOfFile(firstFile) >= 0 && uploadPage.indexOfFile(secondFile) >= 0;

        actual("First: '" + first + "', second: '" + second + "'. Two uploads listed in history: " + bothListed + ". Top rows: " + files.subList(0, Math.min(3, files.size())));
        Assert.assertTrue(bothListed, "Not all uploads are retained in the history");
    }
}
