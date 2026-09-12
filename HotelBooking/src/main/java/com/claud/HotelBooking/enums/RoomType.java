package com.claud.HotelBooking.enums;

public enum RoomType {
    STANDARD_ROOM("Standard Room"),
    DELUXE_SUITE("Deluxe Suite"),
    MENDLS_SUITE("Mendl's Suite"),
    THE_GRAND_SUITE("The Grand Suite");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
