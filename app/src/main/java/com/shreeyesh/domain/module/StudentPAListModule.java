package com.shreeyesh.domain.module;

public class StudentPAListModule {

    private String student_id;
    private String student_name;
    private String attendance_status;
    private String date;
    private String gender;

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getAttendance_status() {
        return attendance_status;
    }

    public void setAttendance_status(String attendance_status) {
        this.attendance_status = attendance_status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

/*  public String student_id = "";
    public String student_name = "";
    public String attendance_status = "";
    public String date = "";
    public String gender = "";


    public static ArrayList<StudentPAListModule> studentPAListModuleArrayList;

    public StudentPAListModule(String student_id, String student_name, String attendance_status, String date, String gender) {
        this.student_id = student_id;
        this.student_name = student_name;
        this.attendance_status = attendance_status;
        this.date = date;
        this.gender = gender;
    }
}*/

