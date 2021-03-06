package com.example.engineerquiz1;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {
    public String username;
    public String email;

    public User()
    {
    }

    public User(String username, String email)
    {
        this.username = username;
        this.email = email;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

}
