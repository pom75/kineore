
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

    public CookieMessage(String status, String message, String token, String admin, String idAdmin) {
        super(status, message);
        this.token = token;
        this.admin = admin;
        this.idAdmin = idAdmin;
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

    public String getIdAdmin() {
        return idAdmin;
    }
}
