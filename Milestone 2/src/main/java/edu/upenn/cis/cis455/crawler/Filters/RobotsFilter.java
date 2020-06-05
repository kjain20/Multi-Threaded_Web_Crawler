package edu.upenn.cis.cis455.crawler.Filters;

import com.google.gson.Gson;
import edu.upenn.cis.cis455.Util.PathMatcher;
import edu.upenn.cis.cis455.crawler.Constants;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.microservice.RobotsMicroService;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainRobotInfo;
import edu.upenn.cis.cis455.model.requestModels.RobotFileModel;

public class RobotsFilter implements CrawlFilter {

    private RobotsMicroService robotsMicroService;
    private PathMatcher pathMatcher;
    private Gson gson = new Gson();

    public RobotsFilter(RobotsMicroService robotsMicroService, PathMatcher pathMatcher) {
        this.robotsMicroService = robotsMicroService;
        this.pathMatcher = pathMatcher;
    }

    @Override
    public void renderRequest(RequestContext requestContext) {
        if(requestContext.isValid() && requestContext.getUrlInfo() != null) {

            DomainRobotInfo domainRobotInfo = robotsMicroService.getDomainInfo(requestContext.getUrlInfo().getURL().getProtocol(),
                    requestContext.getUrlInfo().getHostName(),
                    requestContext.getUrlInfo().getPortNo());

            if(domainRobotInfo == null) {
                requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                requestContext.setDebugMessage("Robot File is Null on Domain");
                return;
            }

            RobotFileModel robotFileModel = new RobotFileModel(domainRobotInfo.getRobotFileString());
            if(robotFileModel == null) {
                requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                requestContext.setDebugMessage("Robot File Structure is null");
                return;
            }

            PathMatcherOutput pathMatcherOutput = pathMatcher.getMatchingPath(requestContext.getUrlInfo(),robotFileModel,
                    Constants.getThis().getAllowedUserAgents());

            if(!pathMatcherOutput.isMatch()) {
                requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                requestContext.setDebugMessage("Robots.txt access denied. No crawling");
                requestContext.setPathMatcherOutput(pathMatcherOutput);
                return;
            }

            requestContext.setRobotFileModel(robotFileModel);
            requestContext.setPathMatcherOutput(pathMatcherOutput);
        }
        else {
            requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
            requestContext.setDebugMessage("Request is not Valid");
            return;
        }
    }

}
