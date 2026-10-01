package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_013: Verify upload summary information.
 * Expected: Summary displays the file name, upload date, and successfully uploaded count.
 */
public class RLC_BU_013 extends BaseTest {

    @Test
    public void uploadSummaryShowsFileNameDateAndCount() throws Exception {
        String fileName = TestData.uniqueFile("bulk_summary", "xlsx");
        Path file = UploadFiles.bulkLeads(fileName, List.of(TestData.adultLead(), TestData.adultLead()));
        Flows.expectSuccess(flows.bulkUpload(flows.bulkCampaign(), file), "Bulk upload");

        List<String> rows = uploadPage.uploadedFiles();
        String summaryRow = rows.stream().filter(r -> r.startsWith(fileName)).findFirst().orElse("(not listed)");
        String view = uploadPage.viewUploadedFile(fileName);

        actual("Summary row: " + summaryRow + ". View: " + view.split("\n")[0] + " / " + view.split("\n")[1]);
        Assert.assertTrue(summaryRow.contains(TestData.today()), "File name / upload date not shown in summary");
        Assert.assertTrue(view.contains("2 total records"), "Successfully uploaded count not shown");
    }
}
