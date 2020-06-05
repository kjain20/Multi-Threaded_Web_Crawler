package edu.upenn.cis.cis455.crawler;

import java.util.ArrayList;
import java.util.List;
import edu.upenn.cis.cis455.crawler.QueuePackage.CrawlerDataStructure;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.Workers.CrawlWorker;
import edu.upenn.cis.cis455.crawler.Workers.WaitQueueWorker;
import edu.upenn.cis.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.microservice.RequestMicroService;
import edu.upenn.cis.cis455.storage.StorageFactory;
import edu.upenn.cis.cis455.storage.StorageImpl;
import edu.upenn.cis.cis455.storage.StorageInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



//This should spawn all worker threads.
public class Crawler implements CrawlMaster {

    static final int NUM_WORKERS = 10;
    private List<CrawlWorker> workers_List =  new ArrayList<>();
    private WaitQueueWorker waitQueueWorker;
    private CrawlerStatus status;
    private CrawlerDataStructure crawlerDataStructure;
    private StorageInterface db;
    private RequestMicroService requestMicroService;
    private int maxDocSize;

    public CrawlerDataStructure getCrawlerDataStructure() {
        return this.crawlerDataStructure;
    }


    //Crawler is Independent Service which runs parallely
    public Crawler(String startUrl, StorageInterface db, int size, int count) {
        // TODO: initialize
        this.crawlerDataStructure = new CrawlerDataStructure(startUrl,0,count);
        this.status = CrawlerStatus.NOT_STARTED;
        this.db = db;
        this.maxDocSize = size * 1024 * 1024;
        this.requestMicroService = new RequestMicroService(this);
    }


    public StorageInterface getDb() {
        return this.db;
    }

    final static Logger logger = LogManager.getLogger(CrawlMaster.class);
    ///// TODO: you'll need to flesh all of this out.  You'll need to build a thread
    // pool of CrawlerWorkers etc. and to implement the functions below which are
    // stubs to compile

    /**
     * Main thread
     */
    public void start() {
        synchronized (this) {
            if (status != CrawlerStatus.NOT_STARTED) {
                return;
            }
            this.status = CrawlerStatus.RUNNING;
            //start cleaner thread to push from ready to wait
            this.waitQueueWorker = new WaitQueueWorker(this, 360);
            waitQueueWorker.start();
            //start worker threads with this object as reference to get the queue(from crawldatastructure).
            for (int i = 0; i < NUM_WORKERS; i++) {
                CrawlWorker workerThread = new CrawlWorker(this); //sending this so that worker can poll and close crawler thread
                workers_List.add(workerThread);
                workerThread.start();
            }
        }
    }

    /**
     * Returns true if it's permissible to access the site right now
     * eg due to robots, etc.
     */
    public boolean isOKtoCrawl(String site, int port, boolean isSecure) {
        return true;
    }

    /**
     * Returns true if the crawl delay says we should wait
     */
    public boolean deferCrawl(String site) {
        return true;
    }

    /**
     * Returns true if it's permissible to fetch the content,
     * eg that it satisfies the path restrictions from robots.txt
     */
    public boolean isOKtoParse(URLInfo url) {
        return true;
    }

    /**
     * Returns true if the document content looks worthy of indexing,
     * eg that it doesn't have a known signature
     */
    public boolean isIndexable(String content) {
        return true;
    }

    /**
     * We've indexed another document
     */
    public void incCount() {

    }

    /**
     * Workers can poll this to see if they should exit, ie the
     * crawl is done
     */
    public boolean isDone() {
        return this.status == CrawlerStatus.TERMINATE;
    }

    /**
     * Workers should notify when they are processing an URL
     */
    public void setWorking(boolean working) {
    }

    /**
     * Workers should call this when they exit, so the master
     * knows when it can shut down
     */
    public void notifyThreadExited() {
    }

    /**
     * Main program:  init database, start crawler, wait
     * for it to notify that it is done, then close.
     */

    //push into ready queue
    public synchronized void pushIntoReadyQueue(ReadyQueueInstance readyQueueInstance) {
        crawlerDataStructure.getReadyQueue().add(readyQueueInstance);
    }

    public static void main(String args[]) {
        if (args.length < 3 || args.length > 5) {
            //System.out.println("Usage: Crawler {start URL} {database environment path} {max doc size in MB} {number of files to index}");
            System.exit(1);
        }

        //System.out.println("Crawler starting");
        String startUrl = args[0];//sandbox url
        String envPath = args[1]; //this is for db ?? -- same as webinterface /www
        Integer size = Integer.valueOf(args[2]);
        Integer count = args.length == 4 ? Integer.valueOf(args[3]) : 100;

        //envPath must be changed to the directory here......................
        StorageImpl db = (StorageImpl)StorageFactory.getDatabaseInstance(envPath);

        Crawler crawler = new Crawler(startUrl, db, size, count);
        //System.out.println("Starting crawl of " + count + " documents, starting at " + startUrl);
        crawler.start(); // crawler will start the worker threads

        while (!crawler.isDone()) {
            try {
                Thread.sleep(10); //sleep before checking if crawler self has to terminate
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // TODO: final shutdown
        try
        {
            db.close();

        }
        catch (Exception e)
        {

        }
        //System.out.println("Done crawling!");
    }




    public void exitThread(CrawlWorker crawlWorker) {
        synchronized (this) {
            this.workers_List.remove(crawlWorker);
        }
        crawlDone();
    }

    public void crawlDone() {
        synchronized (this) {
            if (areAllWorkersNotRunning() && crawlerDataStructure.getQueuesSize() == 0 &&
                    this.status != CrawlerStatus.WAITING_FOR_TERMINATE && this.status != CrawlerStatus.TERMINATE) {
                this.status = CrawlerStatus.WAITING_FOR_TERMINATE;
            }
            if (workers_List.size() == 0 && this.status == CrawlerStatus.WAITING_FOR_TERMINATE) {
                this.status = CrawlerStatus.TERMINATE;
                while (true) {
                    try {
                        this.waitQueueWorker.interrupt();
                        this.waitQueueWorker.join();
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public boolean areAllWorkersNotRunning() {
        for (CrawlWorker crawlerThread : workers_List) {
            if (crawlerThread.getCrawlerThreadStatus() == CrawlWorker.CrawlerThreadState.RUNNING) {
                return false;
            }
        }
        return true;
    }

    public boolean shouldCrawlerTerminate() {
        if (this.status == CrawlerStatus.WAITING_FOR_TERMINATE || this.status == CrawlerStatus.TERMINATE) {
            return true;
        }
        if (areAllWorkersNotRunning() && this.crawlerDataStructure.getQueuesSize() == 0) {
            return true;
        }
        return false;
    }

    public RequestMicroService getRequestMicroService() {
        return this.requestMicroService;
    }


    public CrawlerStatus getStatus() {
        return status;
    }

    public enum CrawlerStatus {
        NOT_STARTED,
        RUNNING,
        WAITING_FOR_TERMINATE,
        TERMINATE;
    }

    public static int getNumWorkers() {
        return NUM_WORKERS;
    }

    public List<CrawlWorker> getWorkers_List() {
        return workers_List;
    }

    public WaitQueueWorker getWaitQueueWorker() {
        return waitQueueWorker;
    }



    public CrawlerStatus getCrawlerStatus() {
        return status;
    }

    public int getMaxDocSize() {
        return maxDocSize;
    }
}
