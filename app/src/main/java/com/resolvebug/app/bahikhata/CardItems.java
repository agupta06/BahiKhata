package com.resolvebug.app.bahikhata;

public class CardItems {

    private String transactionId;
    private String date;
    private String time;
    private String timeZone;
    private String type;
    private String amount;
    private String message;
    private String important;

    public CardItems(String transactionId, String date, String time, String timeZone, String type, String amount, String message, String important) {
        this.transactionId = transactionId;
        this.date = date;
        this.time = time;
        this.timeZone = timeZone;
        this.type = type;
        this.amount = amount;
        this.message = message;
        this.important = important;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImportant() {
        return important;
    }

    public void setImportant(String important) {
        this.important = important;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void changeText(String text) {
        message = text;
    }
}
