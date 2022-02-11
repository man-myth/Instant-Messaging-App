package controller;

public class RegisterController {

    // this will show the GUI for registering the user,
    // and it will add the user to the data file

    private String userName;
    private String password;
    private String reEnteredPass;

    //setters
    public void setPassword(String password) {this.password = password;}
    public void setReEnteredPass(String reEnteredPass) {this.reEnteredPass = reEnteredPass;}
    public void setUserName(String userName) {this.userName = userName;}

    //getters
    public String getPassword() {return password;}
    public String getReEnteredPass() {return reEnteredPass;}
    public String getUserName() {return userName;}
}
