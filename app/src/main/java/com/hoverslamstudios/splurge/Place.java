package com.hoverslamstudios.splurge;


import java.util.ArrayList;

/**
 * Created on 5/17/2017.
 */

public class Place {
    public String id;
    public String latitude;
    public String longitude;
    public String placeId;
    public String name;
    public int priceLevel;
    public int rating;
    public String address;
    public String website;
    public String mapsUrl;
    public Boolean isOpen;
    public ArrayList<String> types = new ArrayList<>();

    public Place() {
    }

    public String getPriceLevelText(int priceInt) {
        switch (priceInt) {
            case 0:
                return "$";
            case 1:
                return "$$";
            case 2:
                return "$$$";
            case 3:
                return "$$$$";
            case 4:
                return "$$$$$";
        }
        return "No Price Data.";
    }
}
