package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_012: Verify upload timestamps.
 * Expected: Records show the applicable upload timestamps and remain distinguishable.
 */
public class RLC_SLII_012 extends BaseTest {

    @Test
    public void uploadTimestampsDistinguishRecords() throws Exception {
        String fileName = TestData.uniqueFile("slii_same", "xlsx");
        String nic = TestData.adultLead().nic();
        String first = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        nic = TestData.adultLead().nic();
        String second = flows.uploadAgain(UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        long records = uploadPage.countOfFile(fileName);

        List<String> timestamps = uploadPage.uploadedFiles().stream().filter(r -> r.startsWith(fileName + " |"))
                .map(r -> r.substring(r.lastIndexOf('|') + 1).trim()).toList();

        actual("First: '" + first + "', second: '" + second + "'. Records of " + fileName + ": " + records + ". Timestamps: " + timestamps);
        Assert.assertEquals(timestamps.size(), 2, "Two records not found");
        Assert.assertNotEquals(timestamps.get(0), timestamps.get(1), "Records cannot be told apart by timestamp");
    }
}
