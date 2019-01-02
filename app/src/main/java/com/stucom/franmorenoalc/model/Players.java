package com.stucom.franmorenoalc.model;

import java.util.List;

public class Players {

    private List<Player> items;

    public Players(List<Player> items) {
        this.items = items;
    }

    public List<Player> getItems() {
        return items;
    }

    public void setItems(List<Player> items) {
        this.items = items;
    }
}
