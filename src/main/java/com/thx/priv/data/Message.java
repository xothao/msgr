package com.thx.priv.data;

import java.io.Serializable;

public class Message {

    private int id;
    private String cipherText;
    private int [] coordinates;
    private boolean decoded;

    public Message(int id, boolean decoded, String cipherText, int [] coord) {
        this.id = id;
        this.decoded = decoded;
        this.cipherText = cipherText;
        this.coordinates = coord;
    }

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
