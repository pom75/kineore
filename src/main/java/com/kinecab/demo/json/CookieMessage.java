
package com.kinecab.demo.json;

public class CookieMessage extends Message {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    private final String token;
    private final String isKine;
    private final String idKineUser;
    private final String idColab;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public CookieMessage(String status, String message, String token, String isKine, String idKineUser, String idColab) {
        super(status, message);
        this.token = token;
        this.isKine = isKine;
        this.idKineUser = idKineUser;
        this.idColab = idColab;
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

    public String getIdColab() {
        return idColab;
    }
}
