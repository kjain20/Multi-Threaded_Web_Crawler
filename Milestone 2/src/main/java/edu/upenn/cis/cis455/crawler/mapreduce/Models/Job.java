package edu.upenn.cis.cis455.crawler.mapreduce.Models;

import java.util.UUID;

public class Job {
    private String jobId = UUID.randomUUID().toString();
    private Object object;

    public Job(String jobId, Object object) {
        this.jobId = jobId;
        this.object = object;
    }

    public Job(Object object) {
        this.object = object;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
