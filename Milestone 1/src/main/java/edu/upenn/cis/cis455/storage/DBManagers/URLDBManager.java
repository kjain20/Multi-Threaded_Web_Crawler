package edu.upenn.cis.cis455.storage.DBManagers;

import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DocInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfoKey;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.URLIdInfo;
import edu.upenn.cis.cis455.model.SeenContent.SeenContent;
import edu.upenn.cis.cis455.storage.StorageImpl;


import java.util.Date;

public class URLDBManager {

    private EntityStore docIdStore;
    private EntityStore urlIdStore;

    private PrimaryIndex<DomainInfoKey, URLIdInfo> urlIdInfoPrimaryIndex;
    private SecondaryIndex<Integer, DomainInfoKey, URLIdInfo> urlIdInfoSecondaryIndex;

    private SecondaryIndex<String, Integer, DocInfo> docInfoSecondaryIndex;
    private PrimaryIndex<Integer, DocInfo> docInfoPrimaryIndex;

    public URLDBManager(Environment environment) {
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);

        docIdStore = new EntityStore(environment, "docIdStore", storeConfig);
        docInfoPrimaryIndex = docIdStore.getPrimaryIndex(Integer.class, DocInfo.class);
        docInfoSecondaryIndex = docIdStore.getSecondaryIndex(docInfoPrimaryIndex, String.class, "hash_md5");

        urlIdStore = new EntityStore(environment, "urlIdStore", storeConfig);
        urlIdInfoPrimaryIndex = urlIdStore.getPrimaryIndex(DomainInfoKey.class, URLIdInfo.class);
        urlIdInfoSecondaryIndex = urlIdStore.getSecondaryIndex(urlIdInfoPrimaryIndex, Integer.class, "docId");

    }


    public DomainInfo getURLDataInfo(DomainInfoKey domainInfoKey, Transaction txn) {
        URLIdInfo urlOnlyInfo = urlIdInfoPrimaryIndex.get(txn, domainInfoKey, LockMode.READ_COMMITTED);
        if (urlOnlyInfo == null) {
            return null;
        }

        DocInfo docOnlyInfo = docInfoPrimaryIndex.get(txn, urlOnlyInfo.getDocId(), LockMode.READ_COMMITTED);

        if (docOnlyInfo == null) {
            urlIdInfoPrimaryIndex.delete(txn, domainInfoKey);
            return null;
        }
        return new DomainInfo(docOnlyInfo, urlOnlyInfo);
    }

    public SeenContent getContentSeenInfo(String key, Transaction txn) {
        DocInfo seenDocument = docInfoSecondaryIndex.get(txn, key, LockMode.READ_COMMITTED);
        if (seenDocument == null) {
            return null;
        }
        SeenContent seenContent = new SeenContent(seenDocument.getHash_md5(), seenDocument.getData());
        return seenContent;

    }


    public DomainInfo insertURLDataInfo(DomainInfo urlDataInfo, Transaction txn) {

        DomainInfo before = getURLDataInfo(urlDataInfo.getDomainInfoKey(), txn);
        if (before != null) {
            DocInfo oldDocOnlyInfo = before.getDocInfo();
            URLIdInfo urlIdInfo = before.getURLIdInfo();
            DocInfo docInfo = insertDocInfo(urlDataInfo.getDocInfo(), txn);
            urlIdInfo = updateUrlId(docInfo, urlIdInfo, txn);
            URLIdInfo temp = urlIdInfoSecondaryIndex.get(txn, oldDocOnlyInfo.getDocId(), LockMode.READ_COMMITTED);
            if (temp == null) {
                docInfoPrimaryIndex.delete(txn, oldDocOnlyInfo.getDocId());
            }
            return new DomainInfo(docInfo, urlIdInfo);

        } else {
            DocInfo docInfo = insertDocInfo(urlDataInfo.getDocInfo(), txn);
            URLIdInfo urlIdInfo = urlDataInfo.getURLIdInfo();
            urlIdInfo.setDocId(docInfo.getDocId());
            urlIdInfo.setCreatedTime(new Date(System.currentTimeMillis()));
            urlIdInfo = updateUrlId(docInfo, urlIdInfo, txn);
            return new DomainInfo(docInfo, urlIdInfo);
        }
    }


    public DocInfo insertDocInfo(DocInfo docInfo, Transaction txn) {

        DocInfo checkIfPresent = docInfoSecondaryIndex.get(txn, docInfo.getHash_md5(), LockMode.READ_COMMITTED);
        if (checkIfPresent != null) {
            return checkIfPresent;
        } else {
            //System.out.println(docInfo.getData());
            docInfoPrimaryIndex.put(txn, docInfo);
            return docInfo;
        }
    }


    public URLIdInfo updateUrlId(DocInfo docInfo, URLIdInfo urlIdInfo, Transaction txn) {
        urlIdInfo.setDocId(docInfo.getDocId());
        urlIdInfo.setLastModifiedTime(new Date());
        urlIdInfoPrimaryIndex.put(txn, urlIdInfo);
        return urlIdInfo;
    }

    public void close()
    {
        try
        {
            this.urlIdStore.close();
            this.docIdStore.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public PrimaryIndex<DomainInfoKey, URLIdInfo> getUrlIdInfoPrimaryIndex() {
        return urlIdInfoPrimaryIndex;
    }

    public PrimaryIndex<Integer, DocInfo> getDocInfoPrimaryIndex() {
        return docInfoPrimaryIndex;
    }

    public static void main(String[] args) {
        StorageImpl storage = new StorageImpl("/Users/karishma/Desktop/GitWorkspace/IWSProject2/555-hw2/www");
        URLDBManager urldbManager =  new URLDBManager(storage.getEnvironment());
//        DomainInfoKey domainInfoKey = urldbManager.urlIdInfoPrimaryIndex.keys().first();
//        URLIdInfo urlIdInfo = urldbManager.urlIdInfoPrimaryIndex.get(domainInfoKey);
//       // int docId = urldbManager.urlIdInfoPrimaryIndex.get(domainInfo).getDocId();
//        ////System.out.println(urldbManager.docInfoSecondaryIndex.
//        int docId = urlIdInfo.getDocId();
//        DocInfo docInfo = urldbManager.docInfoPrimaryIndex.get(docId);
//        DomainInfoKey domainInfoKey1 = new DomainInfoKey("https","dbappserv.cis.upenn.edu",443,"/crawltest/nytimes/","GET");
//        URLIdInfo urlIdInfo1 = urldbManager.urlIdInfoPrimaryIndex.get(domainInfoKey1);
//        int docId1 = urlIdInfo1.getDocId();
//        DocInfo docInfo1 = urldbManager.docInfoPrimaryIndex.get(docId1);
        //System.out.println(urldbManager.urlIdInfoPrimaryIndex.count());
        System.out.println(urldbManager.docInfoPrimaryIndex.count());

    }

}
