package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_006: Verify repeated blacklist upload.
 * Expected: Blacklist file uploads successfully.
 */
public class RLC_BL_006 extends BaseTest {

    @Test
    public void repeatedUploadSucceeds() throws Exception {
        // First upload
        String nic = TestData.adultLead().nic();
        String firstFile = TestData.uniqueFile("blacklist_first", "xlsx");
        String first = flows.uploadAsAgencyAdmin("Blacklisting", UploadFiles.blacklist(firstFile, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic))));
        // Second upload (a different candidate: the same records are refused as already present)
        nic = TestData.adultLead().nic();
        String secondFile = TestData.uniqueFile("blacklist_second", "xlsx");
        String second = flows.uploadAgain(UploadFiles.blacklist(secondFile, List.<Object[]>of(UploadFiles.blacklistRow("AUTOMATION BLACKLIST", nic))));
        boolean bothListed = uploadPage.indexOfFile(firstFile) >= 0 && uploadPage.indexOfFile(secondFile) >= 0;

        actual("First: '" + first + "', second: '" + second + "'. Both listed: " + bothListed);
        Assert.assertTrue(first.contains("Successfully") && second.contains("Successfully"), "Repeated upload failed");
    }
}
