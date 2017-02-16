package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/2/14.
 */

public class CheckRandCodeResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        String result;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
