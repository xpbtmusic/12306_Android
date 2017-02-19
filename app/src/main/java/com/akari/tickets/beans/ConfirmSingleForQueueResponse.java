package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/2/19.
 */

public class ConfirmSingleForQueueResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private boolean submitStatus;

        public boolean isSubmitStatus() {
            return submitStatus;
        }

        public void setSubmitStatus(boolean submitStatus) {
            this.submitStatus = submitStatus;
        }
    }
}
