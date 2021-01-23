
package com.kinecab.demo.db.entity;

public enum Status {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Enum constants
    //~ ----------------------------------------------------------------------------------------------------------------

    FREE, BOOKED, WAITING, CANCELED, POST_IT;

    public static Status stringToStatus(String s){
        switch (s.toLowerCase()){
            case "free" : return FREE;
            case "booked" : return BOOKED;
            case "waiting" :return WAITING;
            case "post_it" :return POST_IT;
            default: return CANCELED;
        }
    }
}
