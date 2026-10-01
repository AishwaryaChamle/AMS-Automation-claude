### Common Requirement 

 ## Url :

    https://192.168.2.80:8443

 ## Users : 
 1. Agency Head (HOB/AH) : User Id : 102117 
 2. Immediate Supervisor(SMBD):User Id: 102118
 3. Agency Admin(AA): 102186
 4. Users of a different branch (no access to WD01 leads), used only for the document access check (RLC_DM_005):
    * Agency Head (AH): 102187
    * Agency Supervisor (AS): 102191
 
 ## Password : Abcd@123 for all users

* Consider TestDaata should be in Sri Lanka 
* Campaign Name, NIC number, Email Id, Contact Number should be Unique

## Create a Campaign

1. Naviagte to the Url
2. Enter UserName: 102186 and Password
3. Click on Login button
4. Click Recruitment Leads Mgmt
5. Click on Campaign Management
6. Click on '+' button
7. Enter Campaign Name
8. Select Start Date and End Date , Start date Should be Previous date of End date 
   * Selection of dates:
      * Click on date picker and select the date of Start date and End date 
9. Enter Campaign Budget 
10. Lead Target should be in under 500 
11. Click on Proceed Button then Ok Button
12. Click on Dowload Sample Template link , Click on Save button 
13. Click on Downloaded Sample Templete , delete Sample Data and Add data as per the Sri lanka save the updated file
14. Click on "Please upload .XLS, .XLSX or .CSV files only" document Uploader and upload updated file
15. Click on "Bulk Upload bulk leads"
16. Click on Ok button 
   * A file without data rows (headers only) is rejected with the message: "Upload Failed - Your file could not be uploaded due to invalid data. Please fix the errors listed below and re-upload the corrected file."
17. Click on Recruitment Leads Mgmt 
18. Click on 3 dots and as per testcases select option
  1. Edit : 
  * Campaign fields (End Date, Budget) are editable only for Upcoming campaigns (start date in the future); for Ongoing / other campaigns they are disabled
  * Select  date picker of End Date , select end date and it should be current or future date or as per the testcases and click on Proceed button
  * You can bulk upload as per steps 12 to 16 
  2. Deactivate : Click on Deactivate 
  3. Bulk : follow steps from 12 to 16


## SLII Management 

1. Navigate to the url with the user id 102186
2. Click on Recruitment Leads Mgmt
3. Click on SLII Exam Management 
4. Clcik on Download Sample Template and Save button
5. Click on downloaded File and open it
6. Filled all the data as per the Sri lanka save it 
7. Click on 'Please upload .xlsx or .xls or .csv files only' and upload updated SLII File
8. Click on Upload Buttton and click on OK

## Recruitment Funnel

1. Navigate to the URL and login with user id 102186
2. Click on Recruitment Leads Mgmt
3. Click on Recruitment Funnel
4. Use Database.md for calculations

## Blacklisting

1. Navigate to the URL and login with user id 102186
2. Click on Blacklisting card
3. Click on Download Sample Template and Click on Save button
4. Click on downloaded file and open it
5. Filled all the data as per the sri lanka
6. Save the updated file
7. Click on 'Please upload .xlsx or .xls or .csv files only' and upload updated Blacklisting File
8. Click on Upload Buttton and click on OK
   * Duplicate NIC in the file: the file is still uploaded, but in View only one row of that NIC has status "Blacklisted"; the repeated row is shown with status "Duplicate"
9. Click on Logout button



## Create Lead 
1. Navigate to URL and Enter HOB/AH Login credentials and click on Login button
2. Click on Recruitment Leads Mgmt
3. Select the campaign which status is Ongoing
4. Click on '+' button 
   * Interview status options on a lead: Agreed for the interview, Cannot be contacted, Not available for the interview (there is no "Reschedule" status)

### Rejected leads
* In the HOB lead list a rejected lead shows "Rejected by <stage>" (e.g. "Rejected by Supervisor"); the Edit icon and Proceed button are not available
* Eligibility of a rejected NIC in a bulk upload: uploaded under another campaign -> "Eligible"; uploaded under the same campaign -> "Rejected"

Recruitnent Support 

1. HOB Flow

* When the age is 22 years or more (age exactly 22 follows this flow)

Navigate to the https://192.168.2.80:8443 
Login :102117 Password:Abcd@123 , Click on Login button
click on Recruitment Leads Mgmt
Select the Comapign which is in Ongoing status
Click on the + button 
Fill the form 
Approval interview status should be : Agreed for the interview
Scehdule date should be current date
Enter the Remark 
Click on Approved button 
click on Ok button 
Logout HOB: 102117 
Login Immediate Supervisor  102118 ,Password:Abcd@123, Click on Login button
Select approved lead form HOB 
Select the current date Enter remark and click on approved button and click on Ok button
Logout 102118
Login HOB 102117 , Password:Abcd@123 , Click on Login Button
Click on Recruitment Leads Mgmt
Click on Procced button 

* When the age is under 22 years

Navigate to the https://192.168.2.80:8443 
Username:102117 Password:Abcd@123 ,Click on Login Button
click on Recruitment Leads Mgmt 
Select the Comapign which is in Ongoing status
Click on the + button 
Fill the form 
Select the option from dropdown which document type user wants to add
Click on uploader section
Upload the Document which is less than 10 mb and should be pdf, png,jpg
Approval interview status should be : Agreed for the interview
Scehdule date should be current date
Enter the Remark 
Click on Approved button 
click on Ok button 
Click on Logout HOB: 102117 
Login Immediate Supervisor  102118 , Password:Abcd@123 , Click on Login button
Select approved lead form HOB 
interview scheduler date shuld be current date
Enter remark and click on approved button and click on Ok button
Click on Logout 102118
Login AA:102186 Password:Abcd@123 , Click on OK button
Select the recently approved request
Interview scheduler date shuld be current date
Enter remark and click on approved button and click on Ok button
Click on Logout 102186
Login HOB 102117 Password:Abcd@123 m Click on Login button
Click on Recruitment Leads Mgmt
Click on Procced button 

2. AS Flow

* When the age is 22 years or more (age exactly 22 follows this flow)

Navigate to the https://192.168.2.80:8443 
Login :102119, Password:Abcd@123 ,click on login button 
click on Recruitment Leads Mgmt
Select the Comapign which is in Ongoing status
Click on the + button 
Fill the form 
Approval interview status should be : Agreed for the interview
Scehdule date should be current date
* Click on Date Picker and select current date
Enter the Remark 
Click on Approve button , Click on OK button
Click on Logout 102119 

Login :102117 Password:Abcd@123 , Click on Login button
Click on edit icon
Click on Approved button 
click on Ok button 
Logout HOB: 102117
Login Immediate Supervisor  102118 ,pAssword:Abcd@123, Click on Login button
Select approved lead form HOB 
Select the current date Enter remark and click on approved button and click on Ok button
Logout 102118
Login HOB 102117 , password:Abcd@123, Click on Login
Click on Recruitment Leads Mgm
Click on Procced button 

* When the age is under 22 years

Navigate to the https://192.168.2.80:8443 
Enter Credentials Login :102119,Pasword:Abcd@123,Click on OK button
click on Recruitment Leads Mgmt
Select the Comapign which is in Ongoing status
Click on the + button 
Fill the form 
Approval interview status should be : Agreed for the interview
Schedule date should be current date
Enter the Remark 
Click Approved  button, Ok button
Click on Logout 102119 

Login :102117
Click on edit icon
Click on Approved button 
click on Ok button 
Click on Logout HOB: 102117
Login Immediate Supervisor  102118 password:Abcd@123 , Click on Login button
Select approved lead form HOB 
interview scheduler date shuld be current
Enter remark and click on approved button and click on Ok button
Click onLogout 102118

Login AA:102186
Select the recently approved request
interview scheduler date should be current
Enter remark and click on approved button and click on Ok button
Click on Logout 102186
Click on Login HOB 102117, Password:Abcd@123, Click on Login button
Click on Recruitment Leads Mgmt
Click on Procced button 



