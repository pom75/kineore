
package com.kinecab.demo.json;

public class CookieMessage extends Message {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    private final String token;
    private final String admin;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public CookieMessage(String status, String message, String token, String admin) {
        super(status, message);
        this.token = token;
        this.admin = admin;

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
