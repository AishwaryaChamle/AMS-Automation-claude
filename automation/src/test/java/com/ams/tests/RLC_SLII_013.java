package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_013: Verify upload chronological order.
 * Expected: Upload records are displayed in chronological order.
 */
public class RLC_SLII_013 extends BaseTest {

    @Test
    public void uploadRecordsInChronologicalOrder() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("slii", "xlsx");
        String message = flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(fileName, List.<Object[]>of(UploadFiles.sliiRow(nic, 65))));
        List<String> rows = uploadPage.uploadedFiles();
        List<java.time.LocalDateTime> times = rows.stream()
                .map(r -> java.time.LocalDateTime.parse(r.substring(r.lastIndexOf('|') + 1).trim(), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss")))
                .toList();
        boolean newestFirst = true;
        for (int i = 1; i < times.size(); i++) {
            newestFirst = newestFirst && !times.get(i).isAfter(times.get(i - 1));
        }

        int position = uploadPage.indexOfFile(fileName);

        actual("Upload: '" + message + "'. Position of the upload just made: " + position + " (0 = top); list sorted newest first: " + newestFirst);
        Assert.assertTrue(position >= 0 && position < 3, "Latest upload is not among the newest records at the top");
        Assert.assertTrue(newestFirst, "Upload records are not in chronological order");
    }
}
