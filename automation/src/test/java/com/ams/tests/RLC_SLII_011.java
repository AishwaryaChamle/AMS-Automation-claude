package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_011: Verify separate upload records.
 * Expected: Both uploads are handled as separate upload records where permitted, without incorrectly overwriting upload history.
 */
public class RLC_SLII_011 extends BaseTest {

    @Test
    public void sameFileNameKeptAsSeparateRecords() throws Exception {
        String fileName = TestData.uniqueFile("slii_same", "xlsx");
        String nic = TestData.adultLead().nic();
        String first = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        nic = TestData.adultLead().nic();
        String second = flows.uploadAgain(UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        long records = uploadPage.countOfFile(fileName);

        actual("Uploads: '" + first + "', '" + second + "'. Records with the name " + fileName + ": " + records);
        Assert.assertEquals(records, 2L, "Uploads with the same file name are not kept as separate records");
    }
}
