package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/1/5.
 */

public class OrderParam {
    private String passengerTicketStr;
    private String oldPassengerStr;
    private String randCode;
    private String purpose_codes;
    private String key_check_isChange;
    private String leftTicketStr;
    private String train_location;
    private String choose_seats;
    private String seatDetailType;
    private String roomType;
    private String dwAll;
    private String _json_att;
    private String REPEAT_SUBMIT_TOKEN;

    public String getPassengerTicketStr() {
        return passengerTicketStr;
    }

    public void setPassengerTicketStr(String passengerTicketStr) {
        this.passengerTicketStr = passengerTicketStr;
    }

    public String getOldPassengerStr() {
        return oldPassengerStr;
    }

    public void setOldPassengerStr(String oldPassengerStr) {
        this.oldPassengerStr = oldPassengerStr;
    }

    public String getRandCode() {
        return randCode;
    }

    public void setRandCode(String randCode) {
        this.randCode = randCode;
    }

    public String getPurpose_codes() {
        return purpose_codes;
    }

    public void setPurpose_codes(String purpose_codes) {
        this.purpose_codes = purpose_codes;
    }

    public String getKey_check_isChange() {
        return key_check_isChange;
    }

    public void setKey_check_isChange(String key_check_isChange) {
        this.key_check_isChange = key_check_isChange;
    }

    public String getLeftTicketStr() {
        return leftTicketStr;
    }

    public void setLeftTicketStr(String leftTicketStr) {
        this.leftTicketStr = leftTicketStr;
    }

    public String getTrain_location() {
        return train_location;
    }

    public void setTrain_location(String train_location) {
        this.train_location = train_location;
    }

    public String getChoose_seats() {
        return choose_seats;
    }

    public void setChoose_seats(String choose_seats) {
        this.choose_seats = choose_seats;
    }

    public String getSeatDetailType() {
        return seatDetailType;
    }

    public void setSeatDetailType(String seatDetailType) {
        this.seatDetailType = seatDetailType;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getDwAll() {
        return dwAll;
    }

    public void setDwAll(String dwAll) {
        this.dwAll = dwAll;
    }

    public String get_json_att() {
        return _json_att;
    }

    public void set_json_att(String _json_att) {
        this._json_att = _json_att;
    }

    public String getREPEAT_SUBMIT_TOKEN() {
        return REPEAT_SUBMIT_TOKEN;
    }

    public void setREPEAT_SUBMIT_TOKEN(String REPEAT_SUBMIT_TOKEN) {
        this.REPEAT_SUBMIT_TOKEN = REPEAT_SUBMIT_TOKEN;
    }
}
