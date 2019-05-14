package com.shreeyesh.domain.module;

public class TeacherListModule {
    private String user_name;
    private String contact_number1;
    private String user_id;
    private String email_id;
    private String full_name;
    private boolean isSelected = false;

    public String getUser_name ()
    {
        return user_name;
    }

    public void setUser_name (String user_name)
    {
        this.user_name = user_name;
    }

    public String getContact_number1 ()
    {
        return contact_number1;
    }

    public void setContact_number1 (String contact_number1)
    {
        this.contact_number1 = contact_number1;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

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


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
