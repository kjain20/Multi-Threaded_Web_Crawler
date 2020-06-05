package edu.upenn.cis.cis455.storage;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

@Persistent
public class DocumentIndexKey {

    @KeyField(1) String channelName;
    @KeyField(2) Integer docId;

    public DocumentIndexKey() {
    }

    public DocumentIndexKey(String channelName, Integer docId) {
        this.channelName = channelName;
        this.docId = docId;
    }
}
