package com.roll.clientserverhttp_fragments.entities;

import java.util.ArrayList;

/**
 * Created by RDL on 28/02/2017.
 */

public class Contacts {
    private ArrayList<User> contacts = new ArrayList<>();

    public Contacts() {
    }

    public Contacts(ArrayList<User> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<User> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<User> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "contacts{" +
                "contacts=" + contacts +
                '}';
    }
}
