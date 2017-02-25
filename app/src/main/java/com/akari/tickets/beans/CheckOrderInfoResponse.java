package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/2/19.
 */

public class CheckOrderInfoResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private boolean submitStatus;
        private String ifShowPassCode;

        public boolean isSubmitStatus() {
            return submitStatus;
        }

        public void setSubmitStatus(boolean submitStatus) {
            this.submitStatus = submitStatus;
        }

        public String getIfShowPassCode() {
            return ifShowPassCode;
        }

        public void setIfShowPassCode(String ifShowPassCode) {
            this.ifShowPassCode = ifShowPassCode;
        }
    }
}
