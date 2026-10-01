package com.ams.utils;

import com.ams.base.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads counts from the AMS MySQL database (connection and queries from Database.md).
 */
public final class Database {

    /** Recruitment Funnel stage -> query that counts the records in that stage (Database.md). */
    public static final Map<String, String> FUNNEL_QUERIES = new LinkedHashMap<>();

    static {
        FUNNEL_QUERIES.put("PROSPECT", "SELECT COUNT(*) AS count FROM al_recruitment_lead_details"
                + " WHERE lead_status IN ('proceeded', 'eligible', 'moved to onboarding')");
        FUNNEL_QUERIES.put("HOB INTERVIEW", "SELECT COUNT(*) AS count FROM al_recruitment_lead_details"
                + " WHERE interview_status IS NOT NULL AND interview_status != ''");
        FUNNEL_QUERIES.put("SUPERVISOR INTERVIEW", "SELECT COUNT(*) AS count FROM al_recruitment_lead_details"
                + " WHERE supervisor_interview_status IS NOT NULL AND supervisor_interview_status != ''");
        FUNNEL_QUERIES.put("SLII EXAM", "SELECT COUNT(*) AS count FROM al_recruitment_slii_exam_results WHERE status = 'Attended'");
        FUNNEL_QUERIES.put("CODE ISSUED", "SELECT COUNT(*) AS count FROM al_ob_witness_details WHERE is_agent_agreement_uploaded = 1");
        FUNNEL_QUERIES.put("CODE ACTIVATED", "SELECT COUNT(*) AS count FROM al_agent_master WHERE is_active = 1");
        FUNNEL_QUERIES.put("CLEARANCE", "SELECT COUNT(*) AS count FROM al_agent_master WHERE status = 'cleared'");
    }

    private Database() {
    }

    /** Runs every funnel query and returns stage -> count. */
    public static Map<String, Long> funnelCounts() throws Exception {
        Map<String, Long> counts = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(Config.dbUrl(), Config.dbUser(), Config.dbPassword());
             Statement statement = connection.createStatement()) {
            for (Map.Entry<String, String> stage : FUNNEL_QUERIES.entrySet()) {
                try (ResultSet result = statement.executeQuery(stage.getValue())) {
                    result.next();
                    counts.put(stage.getKey(), result.getLong("count"));
                }
            }
        }
        return counts;
    }
}
