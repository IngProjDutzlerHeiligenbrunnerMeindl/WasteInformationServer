package com.wasteinformationserver.website.datarequests.login;

public class LoginState {
    private LoginState() {}

   private static LoginState mythis=new LoginState();

    public static LoginState getObject(){
        return mythis;
    }

    private String username;
    private String firstname;
    private String lastname;
    private String email;

    boolean loggedin = true; // TODO: 05.12.19 set back!! 

    public void logIn(){
        loggedin=true;
    }

    public void logOut(){
        loggedin=false;
    }

    public void setAccountData(String username, String firstname, String lastname, String email){
        this.username=username;
        this.firstname=firstname;
        this.lastname=lastname;
        this.email=email;
    }

    public boolean isLoggedIn(){
        return loggedin;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }
}
