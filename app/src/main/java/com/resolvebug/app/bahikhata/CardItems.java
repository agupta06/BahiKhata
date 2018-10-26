package com.resolvebug.app.bahikhata;

public class CardItems {

    private String amount;
    private String message;

    public CardItems(String amount, String message) {
        this.amount = amount;
        this.message = message;
    }

    public String getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }
}
