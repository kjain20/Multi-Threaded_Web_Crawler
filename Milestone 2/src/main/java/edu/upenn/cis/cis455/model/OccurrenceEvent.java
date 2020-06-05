package edu.upenn.cis.cis455.model;

import edu.upenn.cis.cis455.crawler.mapreduce.Models.EventNode;

/**
 * TODO: this class encapsulates the data from a keyword "occurrence"
 */
public class OccurrenceEvent {

    EventNode eventNode;
    boolean removeEvent = false;
    boolean isNewRecord = false;

    public OccurrenceEvent(EventNode eventNode) {
        this.eventNode = eventNode;
    }

    public void setEventNode(EventNode eventNode) {
        this.eventNode = eventNode;
    }

    public void setRemoveEvent(boolean removeEvent) {
        this.removeEvent = removeEvent;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public EventNode getEventNode() {
        return eventNode;
    }

    public boolean isRemoveEvent() {
        return removeEvent;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }


}
