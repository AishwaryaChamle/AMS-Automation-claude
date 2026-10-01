package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_008: Verify multiple blacklist records.
 * Expected: Multiple upload records are retained and visible.
 */
public class RLC_BL_008 extends BaseTest {

    @Test
    public void multipleUploadsRetainedInHistory() throws Exception {
        // First upload
        String nic = TestData.adultLead().nic();
        String firstFile = TestData.uniqueFile("blacklist_first", "xlsx");
        String first = flows.uploadAsAgencyAdmin("Blacklisting", UploadFiles.blacklist(firstFile, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic))));
        // Second upload (a different candidate: the same records are refused as already present)
        nic = TestData.adultLead().nic();
        String secondFile = TestData.uniqueFile("blacklist_second", "xlsx");
        String second = flows.uploadAgain(UploadFiles.blacklist(secondFile, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic))));
        List<String> files = uploadPage.uploadedFiles();
        boolean bothListed = uploadPage.indexOfFile(firstFile) >= 0 && uploadPage.indexOfFile(secondFile) >= 0;

        actual("First: '" + first + "', second: '" + second + "'. Two uploads listed in history: " + bothListed + ". Top rows: " + files.subList(0, Math.min(3, files.size())));
        Assert.assertTrue(bothListed, "Not all uploads are retained in the history");
    }
}
