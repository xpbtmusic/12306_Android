package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/1/2.
 */

public class Passenger {
    private String passenger_type_name;
    private String isUserSelf;
    private String passenger_name;
    private String idCard;
    private String phoneNum;

    public void setPassenger_type_name(String passenger_type_name) {
        this.passenger_type_name = passenger_type_name;
    }

    public void setIsUserSelf(String isUserSelf) {
        this.isUserSelf = isUserSelf;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    public String getPassenger_type_name() {
        return passenger_type_name;
    }

    public String getIsUserSelf() {
        return isUserSelf;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
