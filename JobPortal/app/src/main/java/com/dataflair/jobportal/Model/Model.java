package com.dataflair.jobportal.Model;

public class Model {

    String id, mail, name, profilepic, role, aboutJob, addationalInfo, adminId, userName,resumeLink,userId;
    String companyName, jobLastDate, jobSalary, jobStartDate, jobTitle, skillsRequired, totalOpenings;

    public Model() {
    }

    public Model(String id, String mail, String name, String profilepic, String role, String aboutJob, String addationalInfo, String adminId, String userName, String resumeLink, String userId, String companyName, String jobLastDate, String jobSalary, String jobStartDate, String jobTitle, String skillsRequired, String totalOpenings) {
        this.id = id;
        this.mail = mail;
        this.name = name;
        this.profilepic = profilepic;
        this.role = role;
        this.aboutJob = aboutJob;
        this.addationalInfo = addationalInfo;
        this.adminId = adminId;
        this.userName = userName;
        this.resumeLink = resumeLink;
        this.userId = userId;
        this.companyName = companyName;
        this.jobLastDate = jobLastDate;
        this.jobSalary = jobSalary;
        this.jobStartDate = jobStartDate;
        this.jobTitle = jobTitle;
        this.skillsRequired = skillsRequired;
        this.totalOpenings = totalOpenings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAboutJob() {
        return aboutJob;
    }

    public void setAboutJob(String aboutJob) {
        this.aboutJob = aboutJob;
    }

    public String getAddationalInfo() {
        return addationalInfo;
    }

    public void setAddationalInfo(String addationalInfo) {
        this.addationalInfo = addationalInfo;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResumeLink() {
        return resumeLink;
    }

    public void setResumeLink(String resumeLink) {
        this.resumeLink = resumeLink;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobLastDate() {
        return jobLastDate;
    }

    public void setJobLastDate(String jobLastDate) {
        this.jobLastDate = jobLastDate;
    }

    public String getJobSalary() {
        return jobSalary;
    }

    public void setJobSalary(String jobSalary) {
        this.jobSalary = jobSalary;
    }

    public String getJobStartDate() {
        return jobStartDate;
    }

    public void setJobStartDate(String jobStartDate) {
        this.jobStartDate = jobStartDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public String getTotalOpenings() {
        return totalOpenings;
    }

    public void setTotalOpenings(String totalOpenings) {
        this.totalOpenings = totalOpenings;
    }


}
