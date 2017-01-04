package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/1/4.
 */

public class QueryParam {
    private String url;
    private String from_station;
    private String to_station;
    private String train_code;
    private String train_date;
    private String purpose_codes;
    private String[] seats;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFrom_station() {
        return from_station;
    }

    public void setFrom_station(String from_station) {
        this.from_station = from_station;
    }

    public String getTo_station() {
        return to_station;
    }

    public void setTo_station(String to_station) {
        this.to_station = to_station;
    }

    public String getTrain_code() {
        return train_code;
    }

    public void setTrain_code(String train_code) {
        this.train_code = train_code;
    }

    public String getTrain_date() {
        return train_date;
    }

    public void setTrain_date(String train_date) {
        this.train_date = train_date;
    }

    public String getPurpose_codes() {
        return purpose_codes;
    }

    public void setPurpose_codes(String purpose_codes) {
        this.purpose_codes = purpose_codes;
    }

    public String[] getSeats() {
        return seats;
    }

    public void setSeats(String[] seats) {
        this.seats = seats;
    }
}
