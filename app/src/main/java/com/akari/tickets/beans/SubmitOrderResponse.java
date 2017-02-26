package com.akari.tickets.beans;

/**
 * Created by Akari on 2017/2/26.
 */

public class SubmitOrderResponse {
    private String[] messages;
    private boolean status;

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
