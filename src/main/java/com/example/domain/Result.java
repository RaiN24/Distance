package com.example.domain;

public class Result {
    String[] destination_addresses;
    String[] origin_addresses;
    String status;
    Answer[] rows;

    public String[] getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(String[] destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public String[] getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(String[] origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Answer[] getRows() {
        return rows;
    }

    public void setRows(Answer[] rows) {
        this.rows = rows;
    }
}
