package com.shreeyesh.domain.module;

public class StudentAttendanceListMonthModule {
    private String date;
    private String student_id;
    private String attendance_status;

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getStudent_id ()
    {
        return student_id;
    }

    public void setStudent_id (String student_id)
    {
        this.student_id = student_id;
    }

    public String getAttendance_status ()
    {
        return attendance_status;
    }

    public void setAttendance_status (String attendance_status)
    {
        this.attendance_status = attendance_status;
    }
}
