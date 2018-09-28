package com.AIO;

import java.util.Date;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;

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
        // TODO: get the time too
        //String time = new MailDateFormat().format(date);

        return full ? fullDate : fullDate.substring(4, 10);
    }

    public String getContent() {
        return content;
    }
}
