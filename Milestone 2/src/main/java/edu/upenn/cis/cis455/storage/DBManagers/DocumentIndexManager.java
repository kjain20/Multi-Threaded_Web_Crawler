package edu.upenn.cis.cis455.storage.DBManagers;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.*;
import com.sleepycat.persist.model.Entity;
import edu.upenn.cis.cis455.model.ChannelsDatabaseModel.ChannelsInfo;
import edu.upenn.cis.cis455.storage.DocumentIndexKey;
import edu.upenn.cis.cis455.storage.DocumentIndexValue;

import java.util.ArrayList;
import java.util.List;

public class DocumentIndexManager {

    private PrimaryIndex<DocumentIndexKey, DocumentIndexValue> pIdx;
    private SecondaryIndex<String, DocumentIndexKey, DocumentIndexValue> sIdxChannel;
    private SecondaryIndex<Integer, DocumentIndexKey, DocumentIndexValue> sIdxDocument;
    private EntityStore channel_store_object;

    public DocumentIndexManager(Environment environment) {

        StoreConfig config = new StoreConfig();
        config.setAllowCreate(true);
        config.setTransactional(true);
        this.channel_store_object = new EntityStore(environment, "temp", config);
        this.pIdx = this.channel_store_object.getPrimaryIndex(DocumentIndexKey.class, DocumentIndexValue.class);
        this.sIdxChannel = this.channel_store_object.getSecondaryIndex(this.pIdx, String.class, "channelName");
        this.sIdxDocument = this.channel_store_object.getSecondaryIndex(this.pIdx, Integer.class, "docId");
    }


    public DocumentIndexValue insert(DocumentIndexKey documentIndexKey) {
        DocumentIndexValue documentIndexValue = new DocumentIndexValue(documentIndexKey);
        pIdx.put(documentIndexValue);
        return documentIndexValue;
    }

    public List<DocumentIndexValue> getDocumentsForChannel(String channelName) {
        List<DocumentIndexValue> documentIndexValueList =  new ArrayList<>();
        EntityCursor<DocumentIndexValue> entityCursor = sIdxChannel.subIndex(channelName).entities();
        try {

            for(DocumentIndexValue documentIndexValue: entityCursor) {
                documentIndexValueList.add(documentIndexValue);
            }

        }finally {
            entityCursor.close();
        }
        return documentIndexValueList;
    }

    public void deleteIndexForDocument(Integer documentId) {
        sIdxDocument.delete(documentId);
    }


}
