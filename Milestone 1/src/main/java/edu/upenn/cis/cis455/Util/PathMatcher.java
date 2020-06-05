package edu.upenn.cis.cis455.Util;

import edu.upenn.cis.cis455.crawler.Filters.PathMatcherOutput;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.requestModels.RobotFileModel;

import java.util.List;

public class PathMatcher {

    public PathMatcherOutput getMatchingPath(URLInfo urlInfo, RobotFileModel robotFileModel, List<String> allowedUserAgents) {
        for (String userAgent: allowedUserAgents) {
            boolean allowed = pathMatch(robotFileModel, userAgent, urlInfo);
            if(allowed){
                return new PathMatcherOutput(true, userAgent);
            } else if(robotFileModel.getAllowedLinks().containsKey(userAgent)) {
                return new PathMatcherOutput();
            }
        }
        return new PathMatcherOutput();
    }


    private boolean pathMatch(RobotFileModel robotFileModel, String agentName, URLInfo urlInfo) {
        if(!robotFileModel.getAllowedLinks().containsKey(agentName)) {
            return false;
        }
        for (String pattern: robotFileModel.getAllowedLinks(agentName)) {
            if(UserAgentMatcher.match(pattern, urlInfo.getFilePath())) {
                return true;
            }
        }
        for(String pattern: robotFileModel.getDisallowedLinks(agentName)) {
            if(UserAgentMatcher.match(pattern, urlInfo.getFilePath())) {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        PathMatcher pathMatcher = new PathMatcher();
    }
}
