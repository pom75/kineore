
package com.kinecab.demo.json;

public class CookieMessage extends Message {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    private final String token;
    private final String admin;
    private final String idAdmin;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public CookieMessage(String status, String message, String token, String admin, String diAdminn) {
        super(status, message);
        this.token = token;
        this.admin = admin;
        this.idAdmin = diAdminn;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public String getAdmin() {
        return admin;
    }

    public String getToken() {
        return token;
    }
}
