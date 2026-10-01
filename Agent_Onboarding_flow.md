I’ve structured your content into a **clean Agent Onboarding workflow**, keeping the functional logic and approval routing intact. This is suitable as a **flow/reference document**, not automation preparation.

## 1. Agent Onboarding — AA

### A. Using NIC Number

1. Open browser and navigate to `https://192.168.2.80:8443`.
2. Login as **AA (102186)**.
3. Click **Agent Management**.
4. Click **Agent Onboarding**.
5. Click **+**.
6. Select **NIC**.
7. Enter a **valid and unique Sri Lankan NIC number**.

   * Age must be between **18 and 65 years**.
8. Click **Proceed**.
9. Complete the applicant details form.
10. Click **Next**.
11. Upload the required documents.
12. Click **Next**.
13. Select **Nationality** and **Preferred Language**.
14. Click **Next**.
15. Enter unique:

* Email ID
* Telephone/Mobile number
* WhatsApp number
* Emergency number
* Home number

16. Select:

* City/Town
* Postal Code
* Province

17. Click **Next**.
18. Select organizational details in this order (each drop-down is enabled only after the previous one is chosen):

* Group Name: **ST**
* Cluster 2: **DH**
* Cluster 1: **GS**
* Branch: **WD01**
* Unit: **WD0100**

19. Click **Next**.
20. Select:

* Bank
* Branch
* Account Type

21. Enter a **valid and unique Sri Lankan bank account number**.
22. Upload the required document.
23. Click **Submit**.
24. Click **OK**.

---

### B. Using BRN Number

1. Open browser and navigate to `https://192.168.2.80:8443`.
2. Login as **AA (102186)**.
3. Click **Agent Management**.
4. Click **Agent Onboarding**.
5. Click **+**.
6. Select **Business Registration Number (BRN)**.
7. Enter a **valid and unique Sri Lankan BRN**.
8. Click **Proceed**.
9. Complete the form:

   * Category
   * Designation
   * Title
   * Other required fields
10. Click **Next**.
11. Upload the required documents.
12. Click **Next**.
13. Select:

* Nationality
* Preferred Language
* Company Registration Date

14. Select the Company Registration Date using the date picker.
15. Click **Next**.
16. Enter unique:

* Email ID
* Telephone/Mobile number
* WhatsApp number
* Emergency number
* Home number

17. Select:

* City/Town
* Postal Code
* Province

18. Click **Next**.
19. Select in this order (each drop-down is enabled only after the previous one is chosen):

* Group Name: **ST**
* Cluster 2: **DH**
* Cluster 1: **GS**
* Branch: **WD01**
* Unit: **WD0100**

20. Click **Next**.
21. Select:

* Bank
* Branch
* Account Type

22. Enter a valid and unique Sri Lankan bank account number.
23. Upload the required document.
24. Click **Submit**.
25. Click **OK**.
26. Logout.

---

# 2. Agent Onboarding — AH/HOB

### A. Start Onboarding

1. Open browser and navigate to `https://192.168.2.80:8443`.
2. Login as **AH/HOB (102117)**.
3. Click **Agent Management**.
4. Click **Agent Onboarding**.
5. Click **Edit** for the **New Lead**.
6. If New Lead is not available, perform the **Recruitment Leads Management** flow first.

### B. Applicant Details

7. Select:

   * Category
   * Designation
   * Title
8. Complete the remaining editable fields.
9. Click **Next**.
10. Upload the required documents.

    * Documents should be less than **10 MB**.
11. Click **Next**.

### C. Personal Details

12. Select:

    * Nationality
    * Preferred Language
    * Civil Status
13. If **Civil Status = Married**, upload the required document.
14. Select **Children = Yes/No**.
15. If **Yes**:

    * Select Number of Children.
    * Upload documents according to the number of children.
16. Select **Rejoining = Yes/No**.
17. If **Yes**:

    * Enter the required details.
    * Select **SDM Approval Date**.
18. Click **Next**.

### D. Address Details

19. Enter the editable address fields.
20. If **Corresponding address is same as permanent address** is not selected, enter the correspondence address.
21. Select:

    * City/Town
    * Postal Code
    * Province
    * District
    * Electorate
    * D. Secretariat
    * Grama Niladhari
22. Ensure the Province is **Western**, or the province specified in the testcase.
23. Click **Next**.

### E. Qualification & Examination Details

24. Select qualification status:

    * Pass/Done
    * Fail/Not Done
25. If an examination is **Pass/Done**, upload the required document.
26. If **G.C.E. O/L = Pass/Done**:

    * O/L English Grade becomes available.
    * O/L Maths Grade becomes available.
    * Select the applicable grades.
27. Select applicable **Degree Category** and/or **Language Proficiency**.
28. Select **Extracurricular Activities = Yes/No**.
29. If **Yes**, upload the required document.
30. Check **IBSL Examination Data**.
31. If IBSL data is not auto-populated:

    * Select the option indicating the IBSL exam was completed in another company.
    * Upload the confirmation document.
32. Select:

    * AML
    * TP Test
    * Data Protection Exam
    * Mode
33. Select **Examination Date**.
34. Select **Year**.
35. Click **+** to add the examination.
36. Verify that **Cancel** is available for the added examination.
37. Click **Next**.

### F. Previous Work Experience

38. Select **Previous Work Experience = Yes/No**.
39. If **Yes**, select **Years of Experience**.
40. Select **Previous Work Experience at an Insurance Company = Yes/No**.
41. If **Yes**:

    * Enter the required details.
    * Select the **Period Worked** using the date picker.
    * Upload the **Working Experience Letter**.
    * Click **Save**.
42. Use **+** to add additional work experience.
43. Select **Work Station Details**.
44. Select **Introduced by SO = Yes/No**.
45. If **Yes**, enter an existing and activated **SO/Agent/Sales Code**.
46. Click **Next**.

### G. Bank Details

47. Select:

    * Bank
    * Branch
    * Account Type
48. Enter a valid Sri Lankan bank account number.
49. Upload the required document.
50. Click **Next**.

### H. Dependants for Hospitalization Benefit

51. The dependant section is optional.
52. If adding a dependant, enter/select:

    * Relationship
    * Gender
    * Title
    * Employment
    * Sri Lankan NIC
    * Date
53. Click **Save**.
54. Select the confirmation checkbox for verification against the Group Annuity Proposal Form.
55. Click **Next**.
56. Up to **5 members** can be added using the **+** button.
57. Use a different NIC and relationship for each dependant.

### I. Witness Details

58. Enter witness details.
59. Add the second witness.
60. Use **+** if another witness is required.
61. Click **Save**.
62. Upload the required document.
63. Click **Next**.

### J. Raise Request

64. Verify the **Total**.
65. Select **PEP Question = Yes/No**.
66. Click **Raise Request**.
67. Click **OK**.

---

# 3. Approval Routing

The routing depends on the **Total** and applicant's age.

| Condition               | Approval Tray                     |
| ----------------------- | --------------------------------- |
| Total < 30              | Immediate Supervisor — **102118** |
| Total > 30              | LOP — **102043**                  |
| Total = 30 and Age > 50 | LOP — **102043**                  |

### Immediate Supervisor Route

**HOB/AH → Immediate Supervisor → LOP → AA**

### LOP Route

**HOB/AH → LOP → AA**

---

# 4. Approval Process

1. Logout from **HOB/AH (102117)**.
2. Login according to the routing:

   * Immediate Supervisor: **102118**
   * LOP: **102043**
3. Click **OK** after login.
4. Open **Approval Tray**.
5. Select the application with the **Generated Sales Code**.
6. Click **Next** through the screens until reaching the **Average Recruitment Indicator** screen.
7. Enter remarks.
8. Select either:

   * **Reject**, or
   * **Approve**

### If Rejected

The application follows the system's rejection flow.

### If Approved

**When Immediate Supervisor approves:**

`Immediate Supervisor → LOP → AA`

**When LOP approves:**

`LOP → AA`

---

# 5. Final AA Approval

1. Login as **AA (102186)**.
2. Open **Approval Tray**.
3. Select the application.
4. Review and approve the application.
5. After AA approval, login as **HOB/AH (102117)**.
6. Open **Submitted**.
7. Click the **Edit** icon for the application.
8. Download the Agreement.
9. Upload the signed/completed Agreement.
10. Click **Submit**.
11. The application proceeds to the final approval/completion flow.
