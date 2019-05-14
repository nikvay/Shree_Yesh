package com.shreeyesh.domain.module;

public class StudentAttendanceListModule {
    private String email_id;
    private String full_name;
    private String user_name;
    private String student_id;
    private String contact_number1;
    private String attendance_status;
    private String roll_num;
    private String gender;
    private boolean isSelected = false;
    private boolean isPresent = true;

    public String getEmail_id ()
    {
        return email_id;
    }

    public void setEmail_id (String email_id)
    {
        this.email_id = email_id;
    }

    public String getFull_name ()
    {
        return full_name;
    }

    public void setFull_name (String full_name)
    {
        this.full_name = full_name;
    }

    public String getUser_name ()
    {
        return user_name;
    }

    public void setUser_name (String user_name)
    {
        this.user_name = user_name;
    }

    public String getStudent_id ()
    {
        return student_id;
    }

    public void setStudent_id (String student_id)
    {
        this.student_id = student_id;
    }

    public String getContact_number1 ()
    {
        return contact_number1;
    }

    public void setContact_number1 (String contact_number1)
    {
        this.contact_number1 = contact_number1;
    }

    public String getAttendance_status ()
    {
        return attendance_status;
    }

    public void setAttendance_status (String attendance_status)
    {
        this.attendance_status = attendance_status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public String getRoll_num() {
        return roll_num;
    }

    public void setRoll_num(String roll_num) {
        this.roll_num = roll_num;
    }
}
