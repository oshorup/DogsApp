package com.app.development.dogsapp.model;

public class SendSMS {

   public String to;
   public String text;
   public String imageUrl;

    public SendSMS(String to, String text, String imageUrl) {
        this.to = to;
        this.text = text;
        this.imageUrl = imageUrl;
    }
}
