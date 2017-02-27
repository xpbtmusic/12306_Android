package com.akari.tickets.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akari on 2017/1/4.
 */

public class QueryParam implements Parcelable {
    private String url;
    private String from_station_code;
    private String to_station_code;
    private String from_station;
    private String to_station;
    private String train_code;
    private String train_date;
    private String back_train_date;
    private String purpose_codes;
    private String[] seats;
    private String[] date2;
    private String passenger;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFrom_station_code() {
        return from_station_code;
    }

    public void setFrom_station_code(String from_station) {
        this.from_station_code = from_station;
    }

    public String getTo_station_code() {
        return to_station_code;
    }

    public void setTo_station_code(String to_station) {
        this.to_station_code = to_station;
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

    public String getBack_train_date() {
        return back_train_date;
    }

    public void setBack_train_date(String back_train_date) {
        this.back_train_date = back_train_date;
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

    public String[] getDate2() {
        return date2;
    }

    public void setDate2(String[] date2) {
        this.date2 = date2;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(from_station_code);
        dest.writeString(to_station_code);
        dest.writeString(from_station);
        dest.writeString(to_station);
        dest.writeString(train_code);
        dest.writeString(train_date);
        dest.writeString(back_train_date);
        dest.writeString(purpose_codes);
        dest.writeStringArray(seats);
        dest.writeStringArray(date2);
        dest.writeString(passenger);
    }

    public static final Parcelable.Creator<QueryParam> CREATOR = new Creator<QueryParam>() {
        @Override
        public QueryParam createFromParcel(Parcel source) {
            QueryParam queryParam = new QueryParam();
            queryParam.url = source.readString();
            queryParam.from_station_code = source.readString();
            queryParam.to_station_code = source.readString();
            queryParam.from_station = source.readString();
            queryParam.to_station = source.readString();
            queryParam.train_code = source.readString();
            queryParam.train_date = source.readString();
            queryParam.back_train_date = source.readString();
            queryParam.purpose_codes = source.readString();
            queryParam.seats = source.createStringArray();
            queryParam.date2 = source.createStringArray();
            queryParam.passenger = source.readString();
            return queryParam;
        }

        @Override
        public QueryParam[] newArray(int size) {
            return new QueryParam[size];
        }
    };
}
