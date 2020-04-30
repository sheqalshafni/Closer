package com.sheqal.closer;

public class EventItem {

    private String _eventType;
    private String _countedDays;
    private String _userTitle;
    private String _originalDate;

    public EventItem(String _eType, String _cDays, String _uTitle, String _oriDate){

        _eventType = _eType;
        _countedDays = _cDays;
        _userTitle = _uTitle;
        _originalDate = _oriDate;
    }

    public String get_eventType(){
        return  _eventType;
    }

    public void set_eventType(){
        this._eventType = _eventType;
    }

    public String get_countedDays() {
        return _countedDays;
    }

    public void set_countedDays(String _countedDays) {
        this._countedDays = _countedDays;
    }

    public String get_userTitle() {
        return _userTitle;
    }

    public void set_userTitle(String _userTitle) {
        this._userTitle = _userTitle;
    }

    public String get_originalDate() {
        return _originalDate;
    }

    public void set_originalDate(String _originalDate) {
        this._originalDate = _originalDate;
    }
}
