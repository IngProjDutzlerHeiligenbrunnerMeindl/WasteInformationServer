package com.wasteinformationserver.website.datarequests.login;

/**
 * singleton representing the login state of the user
 */
public class LoginState {

    private static final LoginState mythis = new LoginState();

    /**
     * get object
     * @return LoginState instance
     */
    public static LoginState getObject() {
        return mythis;
    }

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private int permission;

    private boolean loggedin = false;

    /**
     * login the user
     */
    public void logIn() {
        loggedin = true;
    }

    /**
     * logout the user
     */
    public void logOut() {
        loggedin = false;
    }

    /**
     * set the account infos
     * username, firstname, lastname, email and permission level
     */
    public void setAccountData(String username, String firstname, String lastname, String email, int permission) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.permission = permission;
    }

    /**
     * check if user is logged in
     * @return loginstate
     */
    public boolean isLoggedIn() {
        return loggedin;
    }

    /**
     * get username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * get firstname
     * @return firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * get lastname
     * @return lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * get email address
     * @return mail address
     */
    public String getEmail() {
        return email;
    }

    /**
     * get permission level
     * @return level as int
     */
    public int getPermission() {
        return permission;
    }
}
