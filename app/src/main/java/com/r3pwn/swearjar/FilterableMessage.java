package com.r3pwn.swearjar;

public class FilterableMessage {
    private String uid, data;

    public FilterableMessage(String uid, String content) {
        this.uid = uid;
        data = content;
    }

    public String getUid() {
        return uid;
    }

    public int getDollarAmount() {
        int messageCost = 0;
        String[] words = data.toLowerCase().split(" ");
        for (int i = 0; i < words.length; i++) {
            if(words[i].equals("ass") || words[i].contains("-ass")) {
                messageCost++;
            }
            if(words[i].contains("bitch")) {
                messageCost++;
            }
            if(words[i].contains("fuck")) {
                messageCost++;
            }
            if(words[i].contains("shit")) {
                messageCost++;
            }
            if(words[i].contains("cunt")){
                messageCost++;
            }
            if(words[i].contains("cock")){
                messageCost++;
            }
            if(words[i].contains("jackass")){
                messageCost++;
            }
            if(words[i].contains("dumbass")){
                messageCost++;
            }
            if(words[i].contains("asshole")){
                messageCost++;
            }
            if(words[i].contains("faggot")){
                messageCost++;
            }
            if(words[i].contains("whore")){
                messageCost++;
            }
        }
        return messageCost;
    }
}
