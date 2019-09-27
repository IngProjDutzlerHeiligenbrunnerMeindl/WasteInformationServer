package website;

public class LoginState {
    private LoginState() {}

   private static LoginState mythis=new LoginState();

    public static LoginState getObject(){
        return mythis;
    }

    String username;
    String firstname;
    String lastname;
    String email;

    boolean loggedin = false;

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
