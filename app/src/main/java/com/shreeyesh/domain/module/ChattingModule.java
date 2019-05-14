package com.shreeyesh.domain.module;

public class ChattingModule {
    private String date;
    private String user_type;
    private String user_id;
    private String sender_name;
    private String msg_id;
    private String message;
    private String timestamp;

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getUser_type ()
    {
        return user_type;
    }

    public void setUser_type (String user_type)
    {
        this.user_type = user_type;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getSender_name ()
    {
        return sender_name;
    }

    public void setSender_name (String sender_name)
    {
        this.sender_name = sender_name;
    }

    public String getMsg_id ()
    {
        return msg_id;
    }

    public void setMsg_id (String msg_id)
    {
        this.msg_id = msg_id;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }
}
