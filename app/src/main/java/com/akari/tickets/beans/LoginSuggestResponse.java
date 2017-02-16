package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/2/14.
 */

public class LoginSuggestResponse {
    private Data data;
    private String[] messages;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public static class Data {
        String loginCheck;

        public String getLoginCheck() {
            return loginCheck;
        }

        public void setLoginCheck(String loginCheck) {
            this.loginCheck = loginCheck;
        }
    }
}
