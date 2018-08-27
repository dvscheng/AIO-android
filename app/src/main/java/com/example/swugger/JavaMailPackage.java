package com.example.swugger;

import javax.mail.Message;

public class JavaMailPackage {

    private Message message;
    private String from;
    private String subject;
    private String content;

    JavaMailPackage(Message message, String from, String subject, String content) {
        this.message = message;
        this.from = from;
        this.subject = subject;
        this.content = content;
    }

    public Message getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
