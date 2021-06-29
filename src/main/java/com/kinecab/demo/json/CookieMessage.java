
package com.kinecab.demo.json;

public class CookieMessage extends Message {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    private final String token;
    private final String isKine;
    private final String idKineUser;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public CookieMessage(String status, String message, String token, String isKine, String idKineUser) {
        super(status, message);
        this.token = token;
        this.isKine = isKine;
        this.idKineUser = idKineUser;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public String getIsKine() {
        return isKine;
    }

    public String getToken() {
        return token;
    }

    public String getIdKineUser() {
        return idKineUser;
    }
}
