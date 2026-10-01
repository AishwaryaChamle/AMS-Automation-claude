## Database Connection


DBPORT : 3306
HOST : 192.168.2.80
DBNAME : AMSQADB
USER : amsuser
PASSWORD : Admin@123 

## Recruitment Funnel flow.  

Prospect:
SELECT COUNT(*) AS count FROM al_recruitment_lead_details
       WHERE lead_status IN ('proceeded', 'eligible','moved to onboarding');


    
1. HOB interview:
 
SELECT COUNT(*) AS count FROM al_recruitment_lead_details
       WHERE interview_status IS NOT NULL AND interview_status != '';
 
2. MBD interview:
      
SELECT COUNT(*) AS count FROM al_recruitment_lead_details
       WHERE supervisor_interview_status IS NOT NULL AND supervisor_interview_status != '';
 
3. SLII exam:
 
SELECT COUNT(*) AS count FROM al_recruitment_slii_exam_results where status='Attended';
 
4. Code Issured:
 
SELECT COUNT(*) AS count FROM al_ob_witness_details
       WHERE is_agent_agreement_uploaded = 1;
       
5. Code activate:
 
SELECT COUNT(*) AS count FROM al_agent_master
       WHERE is_active = 1;
       
6. Clearance:
 
SELECT COUNT(*) AS count FROM al_agent_master
       WHERE status = 'cleared';