package com.shreeyesh.domain.module;

public class NotesModule {
    private String note_id;
    private String note_title;
    private String teacher_id;
    private String class_id;
    private String note_document;
    private String uploaded_date;
    private String division_id;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote_id ()
    {
        return note_id;
    }

    public void setNote_id (String note_id)
    {
        this.note_id = note_id;
    }

    public String getNote_title ()
    {
        return note_title;
    }

    public void setNote_title (String note_title)
    {
        this.note_title = note_title;
    }

    public String getTeacher_id ()
    {
        return teacher_id;
    }

    public void setTeacher_id (String teacher_id)
    {
        this.teacher_id = teacher_id;
    }

    public String getClass_id ()
    {
        return class_id;
    }

    public void setClass_id (String class_id)
    {
        this.class_id = class_id;
    }

    public String getNote_document ()
    {
        return note_document;
    }

    public void setNote_document (String note_document)
    {
        this.note_document = note_document;
    }

    public String getUploaded_date ()
    {
        return uploaded_date;
    }

    public void setUploaded_date (String uploaded_date)
    {
        this.uploaded_date = uploaded_date;
    }

    public String getDivision_id ()
    {
        return division_id;
    }

    public void setDivision_id (String division_id)
    {
        this.division_id = division_id;
    }
}
