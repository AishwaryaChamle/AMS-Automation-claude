package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_021: Verify duplicate blacklist records.
 * Expected: Duplicate records are handled without incorrectly creating multiple distinct blacklist identities for the same candidate.
 */
public class RLC_BL_021 extends BaseTest {

    @Test
    public void duplicateBlacklistRecordsHandled() throws Exception {
        String nic = TestData.adultLead().nic();
        String fileName = TestData.uniqueFile("blacklist_duplicates", "xlsx");
        Path file = UploadFiles.blacklist(fileName, List.<Object[]>of(UploadFiles.blacklistRow("DUPLICATE ONE", nic), UploadFiles.blacklistRow("DUPLICATE ONE", nic)));
        String message = flows.uploadAsAgencyAdmin("Blacklisting", file);
        String view = message.contains("Successfully") ? uploadPage.viewUploadedFile(fileName) : "(file rejected)";
        // The duplicate row is uploaded too, but only one row of the NIC may have the status "Blacklisted"
        // (the repeated row keeps another status, e.g. "Duplicate")
        List<String> nicRows = view.lines().filter(line -> line.contains(nic)).toList();
        long blacklistedRows = nicRows.stream().filter(line -> line.trim().endsWith("Blacklisted")).count();

        actual("Upload with the same NIC twice: '" + message.replace("\n", " | ") + "'. Rows of the NIC: " + nicRows.size()
                + ", with status Blacklisted: " + blacklistedRows + ". View: " + view.replace("\n", " | "));
        Assert.assertTrue(message.contains("Successfully"), "File with duplicate NIC was not uploaded: " + message);
        Assert.assertEquals(blacklistedRows, 1, "Number of 'Blacklisted' rows for the same NIC");
    }
}
