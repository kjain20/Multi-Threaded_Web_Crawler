package edu.upenn.cis.cis455.model.DomainDataBaseModel;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import edu.upenn.cis.cis455.Util.GenerateMd5Hash;

import java.io.Serializable;

@Entity
public class DocInfo implements Serializable {
    @PrimaryKey(sequence = "DocInfoId")
    private Integer docId;
    private String data;
    private int contentLength = 0;
    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private String hash_md5;

    public DocInfo() {
    }

    public DocInfo(String data, int contentLength) {
        this.data = data;
        this.hash_md5 = GenerateMd5Hash.hash(data);
        this.contentLength = contentLength;
    }

    public DocInfo(Integer docId, String data, int contentLength) {
        this.docId = docId;
        this.data = data;
        this.hash_md5 = GenerateMd5Hash.hash(data);
        this.contentLength = contentLength;
    }

    public Integer getDocId() {
        return docId;
    }

    public String getData() {
        return data;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getHash_md5() {
        return hash_md5;
    }
}
