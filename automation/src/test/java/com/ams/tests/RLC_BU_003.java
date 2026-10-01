package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_003: Verify that the sample template required for bulk lead upload is available for download.
 * Expected: Sample template is made available for download.
 */
public class RLC_BU_003 extends BaseTest {

    @Test
    public void sampleTemplateAvailableForDownload() throws Exception {
        flows.openBulkUpload(flows.bulkCampaign());
        Path template = uploadPage.downloadTemplate();
        List<String> headers = UploadFiles.headersOf(template);

        actual("Downloaded " + template.getFileName() + " with columns " + headers);
        Assert.assertTrue(headers.contains("NIC"), "Template has no NIC column: " + headers);
    }
}
