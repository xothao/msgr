package com.thx.priv.util;

import java.security.SecureRandom;

public class Codes {
    static String [][] codes = {{"He", "looks", "at", "me", "solemnly"},// "Because", "I", "am", "going", "to", "retch"},
            {"I", "grab", "for", "the", "bucket", "and"},// "he", "takes", "it", "from", "me", "his", "whole", "body", "con"},
            {"vulsing", "with", "the", "force", "of", "vomiting"},// "The", "contents", "of", "his", "stomach", "appear"},
            {"like", "matted", "leaves", "and", "I", "shudder"},// "I", "didnt", "know", "wraithberry", "did", "that"},
            {"Theres", "a", "knock", "on", "the", "door"},// "and", "I", "go", "to", "it", "The", "Bomb", "is", "there", "out"},
            {"of", "breath", "I", "let", "her", "in", "and"},// "she", "moves", "past", "me", "straight", "to", "Cardan"},
            {"Here", "she", "says", "pulling", "out", "a", "little"},// "vial", "Its", "clay", "It", "may", "help", "draw"},
            {"out", "and", "contain", "the", "toxins"},
            {"Cardan", "nods", "and", "takes", "it", "from"},// "her", "swallowing", "the", "contents", "with", "a"},
            {"grimace", "It", "tastes", "like", "dirt"}}; //,
//            {"It", "is", "dirt", "she", "informs", "him", "And", "theres", "something", "else", "Two"},
//            {"things", "really", "Grimsen", "was", "already", "gone", "from", "his", "forge", "when", "we", "tried"},
//            {"to", "capture", "him", "We", "have", "to", "assume", "the", "worst", "that", "hes", "with", "Orlagh"},
//            {"Also", "I", "was", "given", "this", "She", "takes", "a", "note", "from", "her", "pocket", "Its", "from"},
//            {"Balekin", "Cannily", "phrased", "but", "breaks", "down", "to", "this", "hes", "offering", "the"},
//            {"antidote", "to", "you", "Jude", "if", "you", "will", "bring", "him", "the", "crown"},
//            {"The", "crown", "Cardan", "opens", "his", "eyes", "and", "I", "realize", "he", "must", "have"},
//            {"closed", "them", "without", "my", "noticing"},
//            {"He", "wants", "you", "to", "take", "it", "to", "the", "gardens", "near", "the", "roses", "the", "Bomb", "says"},
//            {"What", "happens", "if", "he", "doesnt", "get", "the", "antidote", "I", "ask"},
//            {"The", "Bomb", "puts", "the", "back", "of", "her", "hand", "against", "Cardans", "check", "Hes"},
//            {"the", "High", "King", "of", "Elfhame", "he", "has", "the", "strength", "of", "the", "land", "to", "draw", "on"},
//            {"But", "hes", "very", "weak", "already", "And", "I", "dont", "think", "he", "knows", "how", "to", "do", "it"},
//            {"Your", "Majesty"},
//            {"He", "looks", "at", "her", "with", "benevolent", "incomprehension", "Whatever", "do"},
//            {"You", "mean", "I", "just", "took", "a", "mouthful", "of", "the", "land", "at", "your", "behest"},
//            {"I", "think", "about", "what", "shes", "saying", "about", "what", "I", "know", "of", "the", "High"},
//            {"Kings", "powers"}};

    public static String getWord(int row, int col) {
        return codes[row][col];
    }

    public static int[] getRandCoordinates() {
        SecureRandom rand = new SecureRandom();
        int randRow1 = rand.nextInt(codes.length);
        int randCol1 = rand.nextInt(codes[randRow1].length);
        int randRow2 = rand.nextInt(codes.length);
        int randCol2 = rand.nextInt(codes[randRow2].length);
        return new int[]{randRow1, randCol1, randRow2, randCol2};
    }

    public static void main(String [] args) throws Exception {
        int[] coordinates = getRandCoordinates();
        String pass1 = Codes.getWord(coordinates[0], coordinates[1]);
        String pass2 = Codes.getWord(coordinates[2], coordinates[3]);
        String password = pass1 + " " + pass2;

        System.out.println("Word: " + password);
        Encrypt encryptUtil = new Encrypt();

        String plainText = "Test test test test";

        String cipherText = encryptUtil.encrypt(plainText, password);
        System.out.println("Encrypted Text : " + cipherText);

        String decryptedText = encryptUtil.decrypt(cipherText, password);
        System.out.println("DeCrypted Text : " + decryptedText);
    }
}
