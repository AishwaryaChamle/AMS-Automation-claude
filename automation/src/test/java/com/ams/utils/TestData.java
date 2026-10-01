package com.ams.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Creates unique Sri Lankan test data.
 * Campaign name, NIC, e-mail and contact number must be unique (Recruitment_Leads_Mgmt.md).
 */
public final class TestData {

    public static final DateTimeFormatter UI_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Random RANDOM = new Random();
    private static final String[] FIRST_NAMES = {"KASUN", "NIMALI", "CHAMARA", "DILANI", "THARINDU", "SANDUNI", "RUWAN", "ISHARA", "LAHIRU", "HASINI"};
    private static final String[] LAST_NAMES = {"PERERA", "FERNANDO", "SILVA", "BANDARA", "JAYAWARDENA", "WICKRAMASINGHE", "RATHNAYAKE", "DISSANAYAKE"};
    private static final String[] CITIES = {"COLOMBO"};

    private TestData() {
    }

    /** Campaign form values (dates as dd/MM/yyyy). */
    public record Campaign(String name, String startDate, String endDate, String budget, String leadTarget) {
    }

    /** Lead (candidate) form values. */
    public record Lead(String title, String firstName, String lastName, String nic, LocalDate dob,
                       String contact, String email, String address1, String address2, String address3, String city) {

        public String fullName() {
            return firstName + " " + lastName;
        }

        public int age() {
            return java.time.Period.between(dob, LocalDate.now()).getYears();
        }
    }

    // ---------- Campaigns ----------

    /** A campaign that starts today (status Ongoing) and ends in 30 days. */
    public static Campaign ongoingCampaign() {
        LocalDate today = LocalDate.now();
        return new Campaign(campaignName(), today.format(UI_DATE), today.plusDays(30).format(UI_DATE), "150000", "100");
    }

    /** A campaign that starts in the future (status Upcoming). */
    public static Campaign upcomingCampaign() {
        LocalDate start = LocalDate.now().plusDays(5);
        return new Campaign(campaignName(), start.format(UI_DATE), start.plusDays(30).format(UI_DATE), "90000", "50");
    }

    // Parts of a Sri Lankan campaign name
    private static final String[] CAMPAIGN_CITIES = {"Colombo", "Kandy", "Galle", "Jaffna", "Negombo", "Matara", "Kurunegala",
            "Anuradhapura", "Batticaloa", "Trincomalee", "Ratnapura", "Badulla", "Nuwara Eliya", "Kalutara", "Gampaha", "Hambantota"};
    private static final String[] CAMPAIGN_SEASONS = {"Avurudu", "Vesak", "Poson", "Esala", "Deepavali", "Thai Pongal", "Monsoon",
            "Year End", "Midyear", "Spring"};
    private static final String[] CAMPAIGN_TYPES = {"Recruitment Drive", "Career Fair", "Advisor Hiring", "Talent Hunt",
            "Agency Growth", "Walk In Interviews"};
    private static final String[] CAMPAIGN_TEAMS = {"Sahan", "Nimal", "Kasun", "Dilani", "Tharindu", "Sanduni", "Ruwan", "Ishara",
            "Lahiru", "Hasini", "Chamara", "Nadeesha", "Pradeep", "Malsha", "Kavindu", "Anoma", "Suresh", "Thilini", "Janaka", "Rukshan"};

    /**
     * A unique Sri Lankan campaign name, e.g. "Kandy Vesak Career Fair Team Sahan".
     * Only letters are used because the campaign search box ignores digits.
     * At most 50 characters: the application cuts longer names off, so the campaign would not be found.
     */
    public static String campaignName() {
        String name;
        do {
            name = CAMPAIGN_CITIES[RANDOM.nextInt(CAMPAIGN_CITIES.length)] + " "
                    + CAMPAIGN_SEASONS[RANDOM.nextInt(CAMPAIGN_SEASONS.length)] + " "
                    + CAMPAIGN_TYPES[RANDOM.nextInt(CAMPAIGN_TYPES.length)] + " Team "
                    + CAMPAIGN_TEAMS[RANDOM.nextInt(CAMPAIGN_TEAMS.length)];
        } while (name.length() > CAMPAIGN_NAME_MAX_LENGTH);
        return name;
    }

    /** Longest campaign name the application stores in full. */
    private static final int CAMPAIGN_NAME_MAX_LENGTH = 50;

    /** Unique 8-digit O/L index number (the application rejects an index number that is already registered). */
    public static String olIndexNumber() {
        return String.valueOf(10_000_000 + RANDOM.nextInt(90_000_000));
    }

    // ---------- Leads ----------

    /** Candidate older than 22 years. */
    public static Lead adultLead() {
        return leadBornOn(LocalDate.now().minusYears(28 + RANDOM.nextInt(10)).minusDays(RANDOM.nextInt(300)));
    }

    /** Candidate younger than 22 years (supporting documents required). */
    public static Lead youngLead() {
        return leadBornOn(LocalDate.now().minusYears(19).minusDays(10 + RANDOM.nextInt(300)));
    }

    /** Candidate of the given age in years (e.g. 52 for the "age >= 50" onboarding cases). */
    public static Lead leadAged(int years) {
        return leadBornOn(LocalDate.now().minusYears(years).minusDays(10 + RANDOM.nextInt(300)));
    }

    /** Candidate born exactly 22 years ago today. */
    public static Lead leadAgedExactly22() {
        return leadBornOn(LocalDate.now().minusYears(22));
    }

    public static Lead leadBornOn(LocalDate dob) {
        String first = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)] + randomLetters(4);
        String last = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
        String unique = String.valueOf(System.nanoTime() % 100_000_000);
        return new Lead("Mr.", first, last, nicFor(dob), dob,
                "07" + String.format("%08d", RANDOM.nextInt(100_000_000)),
                first.toLowerCase() + "." + unique + "@testmail.lk",
                (1 + RANDOM.nextInt(200)) + " Galle Road", "Kollupitiya", "Western Province",
                CITIES[RANDOM.nextInt(CITIES.length)]);
    }

    /**
     * New-format Sri Lankan NIC: YYYY + day of the year (3 digits) + 5 random digits.
     * The application derives the date of birth and age from it.
     */
    public static String nicFor(LocalDate dob) {
        return dob.getYear() + String.format("%03d", dob.getDayOfYear()) + String.format("%05d", RANDOM.nextInt(100_000));
    }

    /** Same candidate details but a different NIC. */
    public static Lead withNic(Lead lead, String nic) {
        return new Lead(lead.title(), lead.firstName(), lead.lastName(), nic, lead.dob(), lead.contact(), lead.email(),
                lead.address1(), lead.address2(), lead.address3(), lead.city());
    }

    /** Same candidate details but a different contact number. */
    public static Lead withContact(Lead lead, String contact) {
        return new Lead(lead.title(), lead.firstName(), lead.lastName(), lead.nic(), lead.dob(), contact, lead.email(),
                lead.address1(), lead.address2(), lead.address3(), lead.city());
    }

    // ---------- Agent onboarding ----------

    /**
     * Answers on the Qualification step that decide the Average Recruitment Indicator (ARI) total.
     * Grades are A, B, C, S or W; speaksSinhala adds the "2nd Language" points.
     */
    public record AriProfile(String englishGrade, String mathsGrade, boolean speaksSinhala) {
    }

    /** O/L English A, Maths A, Sinhala + English: total 45 for a young unmarried candidate (ARI >= 30, goes to LOP). */
    public static final AriProfile HIGH_ARI = new AriProfile("A", "A", true);

    /** O/L English W, Maths W, English only: total 23 for a candidate aged about 30 (ARI < 30, goes to Sr MBD). */
    public static final AriProfile LOW_ARI = new AriProfile("W", "W", false);

    /** Unique 12-digit bank account number (the form needs at least 10 digits). */
    public static String bankAccountNumber() {
        return "10" + String.format("%010d", Math.abs(System.nanoTime() % 10_000_000_000L));
    }

    /** Unique Sri Lankan mobile number, e.g. 0771234567 */
    public static String mobileNumber(String prefix) {
        return prefix + String.format("%07d", RANDOM.nextInt(10_000_000));
    }

    /** Date in the past typed as digits into the dd/mm/yyyy fields, e.g. "20092026". */
    public static String pastDateDigits(int daysAgo) {
        return LocalDate.now().minusDays(daysAgo).format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    }

    /** Unique Sri Lankan NIC for a person of the given age (not linked to any lead). */
    public static String nicForAge(int years) {
        return nicFor(LocalDate.now().minusYears(years).minusDays(10 + RANDOM.nextInt(300)));
    }

    /** Unique upload file name, e.g. slii_1727180000000.xlsx */
    public static String uniqueFile(String prefix, String extension) {
        // Tests run in parallel: the counter keeps names unique even within the same millisecond
        return prefix + "_" + System.currentTimeMillis() + FILE_COUNTER.incrementAndGet() + "." + extension;
    }

    private static final java.util.concurrent.atomic.AtomicInteger FILE_COUNTER = new java.util.concurrent.atomic.AtomicInteger();

    /** Date shown in list tables, e.g. "24 Oct 2026". */
    public static String listDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.ENGLISH));
    }

    public static String today() {
        return LocalDate.now().format(UI_DATE);
    }

    private static String randomLetters(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append((char) ('A' + RANDOM.nextInt(26)));
        }
        return sb.toString();
    }
}
