package edu.upenn.cis.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class DocumentIndexValue {

    @PrimaryKey
    private DocumentIndexKey documentIndexKey;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String channelName;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer docId;

    public DocumentIndexValue(DocumentIndexKey documentIndexKey) {
        this.documentIndexKey = documentIndexKey;
        this.docId = documentIndexKey.docId;
        this.channelName = documentIndexKey.channelName;
    }

    public DocumentIndexValue() {
    }

    public DocumentIndexKey getDocumentIndexKey() {
        return documentIndexKey;
    }

    public void setDocumentIndexKey(DocumentIndexKey documentIndexKey) {
        this.documentIndexKey = documentIndexKey;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }
}
