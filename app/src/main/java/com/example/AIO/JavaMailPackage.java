package com.example.AIO;

import java.util.Date;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

public class JavaMailPackage {

    private Message message;
    private InternetAddress from;
    private String subject;
    private Date date;
    private String content;

    JavaMailPackage(Message message, InternetAddress from, String subject, Date date, String content) {
        this.message = message;
        this.from = from;
        this.subject = subject;
        this.date = date;
        this.content = content;
    }

    public Message getMessage() {
        return message;
    }

    public InternetAddress getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }

    // TODO: return full or short date
    public String getReadableDate(boolean full) {
        // "EEE MMM dd HH:mm:ss zzz yyyy";
        String fullDate = date.toString();
        return full ? fullDate : fullDate.substring(4, 10);
    }

    public String getContent() {
        return content;
    }
}
