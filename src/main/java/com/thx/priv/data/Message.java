package com.thx.priv.data;

import java.io.Serializable;

public class Message implements Serializable {

    private int id;
    private String cipherText;
    private int [] coordinates;
    private boolean decoded;

    public Message(String cipherText, int[] coordinates) {
        this.cipherText = cipherText;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDecoded() {
        return decoded;
    }

    public void setDecoded(boolean decoded) {
        this.decoded = decoded;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

}
