package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BU_005: Verify that a bulk lead upload file containing blank values in optional fields is accepted without validation errors.
 * Expected: File is accepted without validation errors caused by blank optional fields.
 */
public class RLC_BU_005 extends BaseTest {

    @Test
    public void blankOptionalFieldsAcceptedOnBulkUpload() throws Exception {
        // Address lines and city (optional) are left blank
        Path file = UploadFiles.bulkLeadsOptionalBlank(TestData.uniqueFile("bulk_optional_blank", "xlsx"), TestData.adultLead());
        String message = flows.bulkUpload(flows.bulkCampaign(), file);

        actual("Upload with blank Add_1, Add_2, Add_3 and Add_city: '" + message + "'");
        Assert.assertTrue(message.contains("File Uploaded Successfully"), "File with blank optional fields was rejected: " + message);
    }
}
