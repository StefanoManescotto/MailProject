package org.example;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    public static enum Type{
        OK,
        ADDRESS_UNKNOWN,
        NOT_AN_EMAIL,
        SENT,
        BAD_REQUEST
    }

    private String clientMail;
    private Type requestType;

    public ServerResponse(String clientMail, Type requestType){
        if(!Email.emailVerifier(clientMail)){
            throw new IllegalArgumentException();
        }
        this.clientMail = clientMail;
        this.requestType = requestType;
    }

    public String getClientMail() {
        return clientMail;
    }

    public Type getResponseType() {
        return requestType;
    }
}
