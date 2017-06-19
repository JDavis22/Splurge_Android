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

	public Place() { }

	public String getPriceLevelText(int priceInt) {
		switch(priceInt) {
			case 0:
				return "Price: Free";
			case 1:
				return "Price: Inexpensive";
			case 2:
				return "Price: Moderate";
			case 3:
				return "Price: Expensive";
			case 4:
				return "Price: Very Expensive";
		}
		return "No Price Data.";
	}
}
