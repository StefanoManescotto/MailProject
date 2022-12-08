package org.example;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    public enum Type{
        OK,
        ADDRESS_UNKNOWN,
        NOT_A_EMAIL,
        SENT,
        RMVD,
        BAD_REQUEST,
        SERVER_OFFLINE
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
