package com.ams.utils;

import com.ams.utils.TestData.Lead;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builds the files used in upload tests. Valid Excel files start from the
 * sample templates downloaded from the application (src/test/resources/templates),
 * so the column headers are exactly the ones the application expects.
 */
public final class UploadFiles {

    private static final Path FOLDER = Path.of("target", "upload-files").toAbsolutePath();

    /** Branch code written in bulk-lead files; the server only accepts codes from the branch hierarchy (WD01 = Welimada, HOB 102117's branch). */
    private static final String BULK_BRANCH = "WD01";

    /** Real documents taken from this device (NIC scan, photo, PDFs, signature) used by the onboarding form. */
    private static final Path DEVICE_DOCUMENTS = Path.of("src", "test", "resources", "documents").toAbsolutePath();
    private static final Random RANDOM = new Random();

    private UploadFiles() {
    }

    // ---------- Campaign lead bulk upload ----------

    /** Valid bulk-lead Excel file with the given leads (template columns NIC ... Email). */
    public static Path bulkLeads(String fileName, List<Lead> leads) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Lead lead : leads) {
            rows.add(bulkLeadRow(lead));
        }
        return fromTemplate("campaign-lead-bulk-upload-template.xlsx", fileName, rows);
    }

    /** One row in template order: NIC, Title, First Name, Last Name, Contact_No, Branch, Add_1, Add_2, Add_3, Add_city, Email */
    public static Object[] bulkLeadRow(Lead lead) {
        // NIC and contact number are written as text (numbers are not read correctly by the server)
        return new Object[]{lead.nic(), "MR", lead.firstName(), lead.lastName(),
                lead.contact(), BULK_BRANCH,
                lead.address1(), lead.address2(), lead.address3(), lead.city(), lead.email()};
    }

    /**
     * Bulk-lead file with the optional columns Add_1, Add_2, Add_3 and Add_city left blank
     * (the scenario document lists only Name, NIC, Contact number and Branch as mandatory).
     */
    public static Path bulkLeadsOptionalBlank(String fileName, Lead lead) throws Exception {
        Object[] row = bulkLeadRow(lead);
        for (int i = 6; i <= 9; i++) {
            row[i] = null;
        }
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        return fromTemplate("campaign-lead-bulk-upload-template.xlsx", fileName, rows);
    }

    /** Bulk-lead file written as CSV with the template headers. */
    public static Path bulkLeadsCsv(String fileName, Lead lead) throws Exception {
        String header = "NIC,Title,First Name,Last Name,Contact_No,Branch,Add_1,Add_2,Add_3,Add_city,Email";
        Object[] r = bulkLeadRow(lead);
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < r.length; i++) {
            line.append(i == 0 ? "" : ",").append(r[i]);
        }
        return writeText(fileName, header + "\n" + line + "\n");
    }

    /** Bulk-lead file where each given lead row can be replaced by raw values (for invalid-record tests). */
    public static Path bulkLeadRows(String fileName, List<Object[]> rows) throws Exception {
        return fromTemplate("campaign-lead-bulk-upload-template.xlsx", fileName, rows);
    }

    /** SLII file without the given column. */
    public static Path sliiWithoutColumn(String fileName, String nic, String columnToRemove) throws Exception {
        String[] headers = {"NIC", "Marks", "Mode", "Examination Date", "Admission No"};
        return withoutColumn(fileName, headers, sliiRow(nic, 60), columnToRemove);
    }

    /** Blacklist file without the given column. */
    public static Path blacklistWithoutColumn(String fileName, String nic, String columnToRemove) throws Exception {
        String[] headers = {"SURNAME", "NIC", "AGENT CODE NO", "IBSL LICENSE NO", "DATE OF TERMINATION", "INSURANCE COMPANY", "COMMENTS", "STATUS"};
        return withoutColumn(fileName, headers, blacklistRow("STRUCTURE TEST", nic), columnToRemove);
    }

    private static Path withoutColumn(String fileName, String[] headers, Object[] values, String columnToRemove) throws Exception {
        List<String> keptHeaders = new ArrayList<>();
        List<Object> keptValues = new ArrayList<>();
        for (int i = 0; i < headers.length; i++) {
            if (!headers[i].equals(columnToRemove)) {
                keptHeaders.add(headers[i]);
                keptValues.add(values[i]);
            }
        }
        return plainWorkbook(fileName, keptHeaders.toArray(new String[0]), List.<Object[]>of(keptValues.toArray()));
    }

    /** Bulk-lead file without the given column (to check required-column validation). */
    public static Path bulkLeadsWithoutColumn(String fileName, Lead lead, String columnToRemove) throws Exception {
        String[] headers = {"NIC", "Title", "First Name", "Last Name", "Contact_No", "Branch", "Add_1", "Add_2", "Add_3", "Add_city", "Email"};
        Object[] values = bulkLeadRow(lead);
        List<String> keptHeaders = new ArrayList<>();
        List<Object> keptValues = new ArrayList<>();
        for (int i = 0; i < headers.length; i++) {
            if (!headers[i].equals(columnToRemove)) {
                keptHeaders.add(headers[i]);
                keptValues.add(values[i]);
            }
        }
        return plainWorkbook(fileName, keptHeaders.toArray(new String[0]), List.<Object[]>of(keptValues.toArray()));
    }

    // ---------- SLII exam results ----------

    /** SLII row: NIC, Marks, Mode, Examination Date, Admission No */
    public static Object[] sliiRow(String nic, int marks) {
        return new Object[]{nic, marks, "Sinhala", LocalDate.now().minusDays(7),
                "ADM" + (10000 + RANDOM.nextInt(90000))};
    }

    public static Path slii(String fileName, List<Object[]> rows) throws Exception {
        return fromTemplate("SLII-exam-results-upload-template.xlsx", fileName, rows);
    }

    public static Path sliiCsv(String fileName, String nic, int marks) throws Exception {
        String date = LocalDate.now().minusDays(7).toString();
        return writeText(fileName, "NIC,Marks,Mode,Examination Date,Admission No\n" + nic + "," + marks + ",Sinhala," + date + ",ADM" + (10000 + RANDOM.nextInt(90000)) + "\n");
    }

    // ---------- Blacklisting ----------

    /** Blacklist row: SURNAME, NIC, AGENT CODE NO, IBSL LICENSE NO, DATE OF TERMINATION, INSURANCE COMPANY, COMMENTS, STATUS */
    public static Object[] blacklistRow(String surname, String nic) {
        int code = 10000 + RANDOM.nextInt(90000);
        return new Object[]{surname, nic, "AGT" + code, "IBSL" + code, LocalDate.now().minusMonths(3),
                "Lanka Life Assurance", "Automation blacklist entry", "Blacklisted"};
    }

    public static Path blacklist(String fileName, List<Object[]> rows) throws Exception {
        return fromTemplate("Blacklisting-upload-template.xlsx", fileName, rows);
    }

    public static Path blacklistCsv(String fileName, String surname, String nic) throws Exception {
        String date = LocalDate.now().minusMonths(3).toString();
        return writeText(fileName, "SURNAME,NIC,AGENT CODE NO,IBSL LICENSE NO,DATE OF TERMINATION,INSURANCE COMPANY,COMMENTS,STATUS\n"
                + surname + "," + nic + ",AGT55501,IBSL55501," + date + ",Lanka Life Assurance,Automation CSV entry,Blacklisted\n");
    }

    // ---------- Invalid files (negative tests) ----------

    /** Template with headers only and no data rows. */
    public static Path headersOnly(String template, String fileName) throws Exception {
        return fromTemplate(template, fileName, List.of());
    }

    /** Excel file whose headers do not match any template. */
    public static Path wrongTemplate(String fileName) throws Exception {
        return plainWorkbook(fileName, new String[]{"Employee", "Department", "Salary"},
                List.<Object[]>of(new Object[]{"Nimal", "Finance", 45000}));
    }

    /** A file with an .xlsx name but random bytes inside (corrupted). */
    public static Path corruptedExcel(String fileName) throws Exception {
        byte[] junk = new byte[2048];
        RANDOM.nextBytes(junk);
        Files.createDirectories(FOLDER);
        return Files.write(FOLDER.resolve(fileName), junk);
    }

    /** A zero-byte file. */
    public static Path zeroByteFile(String fileName) throws Exception {
        Files.createDirectories(FOLDER);
        return Files.write(FOLDER.resolve(fileName), new byte[0]);
    }

    /** A plain text file (unsupported format). */
    public static Path textFile(String fileName) throws Exception {
        return writeText(fileName, "This is not a supported upload file.\n");
    }

    /** Small JPG image used as a supporting document for candidates under 22 (the uploader accepts .jpg/.jpeg only). */
    public static Path supportingImage() throws Exception {
        return image("prospect_100_report.jpg", "jpg");
    }

    /** Small PNG image (to check which image formats the document uploader accepts). */
    public static Path pngImage() throws Exception {
        return image("vip_visit_report.png", "png");
    }

    // ---------- Agent onboarding documents (real files from this device) ----------

    /**
     * A document from src/test/resources/documents, e.g. "test_nic.jpg".
     * The onboarding form rejects files under 4 KB or over 10 MB, and the same file in two fields,
     * so every field gets its own document.
     */
    public static Path deviceDocument(String fileName) {
        Path file = DEVICE_DOCUMENTS.resolve(fileName);
        if (!Files.exists(file)) {
            throw new IllegalStateException("Device document not found: " + file);
        }
        return file;
    }

    private static Path image(String fileName, String format) throws Exception {
        Files.createDirectories(FOLDER);
        Path file = FOLDER.resolve(fileName);
        if (!Files.exists(file)) {
            BufferedImage image = new BufferedImage(200, 120, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 120; y++) {
                    image.setRGB(x, y, (x * 255 / 200) << 16 | (y * 255 / 120) << 8 | 120);
                }
            }
            ImageIO.write(image, format, file.toFile());
        }
        return file;
    }

    /** Column headers (first row) of a downloaded Excel file. */
    public static List<String> headersOf(Path excelFile) throws Exception {
        List<String> headers = new ArrayList<>();
        try (InputStream in = Files.newInputStream(excelFile); Workbook workbook = WorkbookFactory.create(in)) {
            for (Cell cell : workbook.getSheetAt(0).getRow(0)) {
                headers.add(cell.toString().trim());
            }
        }
        return headers;
    }

    // ---------- Helpers ----------

    /** Copies a template, removes its sample rows and writes the given rows below the header. */
    private static Path fromTemplate(String template, String fileName, List<Object[]> rows) throws Exception {
        Files.createDirectories(FOLDER);
        Path target = FOLDER.resolve(fileName);
        try (InputStream in = UploadFiles.class.getResourceAsStream("/templates/" + template);
             Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            // Delete the sample data under the header row
            for (int r = sheet.getLastRowNum(); r >= 1; r--) {
                Row row = sheet.getRow(r);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }
            writeRows(workbook, sheet, rows);
            try (OutputStream out = Files.newOutputStream(target)) {
                workbook.write(out);
            }
        }
        return target;
    }

    private static Path plainWorkbook(String fileName, String[] headers, List<Object[]> rows) throws Exception {
        Files.createDirectories(FOLDER);
        Path target = FOLDER.resolve(fileName);
        try (Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            writeRows(workbook, sheet, rows);
            try (OutputStream out = Files.newOutputStream(target)) {
                workbook.write(out);
            }
        }
        return target;
    }

    private static void writeRows(Workbook workbook, Sheet sheet, List<Object[]> rows) {
        // Plain "General" style: template column styles can hide values when the file is read back as text
        org.apache.poi.ss.usermodel.CellStyle plain = workbook.createCellStyle();
        plain.setDataFormat((short) 0);
        int rowIndex = 1;
        for (Object[] values : rows) {
            Row row = sheet.createRow(rowIndex++);
            for (int c = 0; c < values.length; c++) {
                Object value = values[c];
                if (value == null) {
                    continue;
                }
                Cell cell = row.createCell(c);
                cell.setCellStyle(plain);
                if (value instanceof Number number) {
                    cell.setCellValue(number.doubleValue());
                } else if (value instanceof LocalDate date) {
                    // The application parses dates with JavaScript, which understands yyyy-MM-dd text
                    cell.setCellValue(date.toString());
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }
    }

    private static Path writeText(String fileName, String content) throws Exception {
        Files.createDirectories(FOLDER);
        return Files.writeString(FOLDER.resolve(fileName), content);
    }
}
