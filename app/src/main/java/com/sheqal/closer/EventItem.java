package com.sheqal.closer;

public class EventItem {

    private String eventTitle, remainingDays, userTitle, originalDate;

    public EventItem(String _eventTitle, String _remainingDays, String _userTitle, String _originalDate){
        eventTitle = _eventTitle;
        remainingDays = _remainingDays;
        userTitle = _userTitle;
        originalDate = _originalDate;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(String remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(String originalDate) {
        this.originalDate = originalDate;
    }
}
