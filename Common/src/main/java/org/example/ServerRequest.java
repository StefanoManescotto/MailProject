package org.example;

import java.io.Serializable;

public class ServerRequest implements Serializable {
    public enum Type{
        AUTHENTICATE,
        SEND_EMAIL,
        RCV_INBOX_EMAILS,
        RCV_SENT_EMAILS,
        RMV_EMAIL
    }

    private String clientMail;
    private Type requestType;

    public ServerRequest(String clientMail, Type requestType){
        if(!Email.emailVerifier(clientMail)){
            throw new IllegalArgumentException();
        }
        this.clientMail = clientMail;
        this.requestType = requestType;
    }

    public String getClientMail() {
        return clientMail;
    }

    public Type getRequestType() {
        return requestType;
    }
}
