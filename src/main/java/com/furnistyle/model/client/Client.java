package com.furnistyle.model.client;

import java.io.Serializable;
import java.util.UUID;

public class Client implements Serializable {
    private final String id;
    private String fullName;
    private String phone;

    public Client(String fullName, String phone) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return fullName + " (" + phone + ")";
    }
}
