package com.example.foodhunter.models;

public class User {
    private String userId, userName, userEmail,userContact,userPass,userDp,userAddress;
    private boolean isAdmin;
    public User() {

    }
    public User(String userId, String userName, String userEmail, String userContact, String userPass, boolean isAdmin, String userAddress) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userContact = userContact;
        this.userPass = userPass;
        this.isAdmin = isAdmin;
        this.userAddress = userAddress;
    }

    public User(String userName, String userEmail, String userContact, String userDp, boolean isAdmin, String userAddress) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userContact = userContact;
        this.userDp = userDp;
        this.isAdmin = isAdmin;
        this.userAddress  = userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserContact() {
        return userContact;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getUserDp() {
        return userDp;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public String getUserAddress() { return userAddress; }
}
