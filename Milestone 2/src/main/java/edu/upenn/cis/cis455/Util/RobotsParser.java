package edu.upenn.cis.cis455.Util;

import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.requestModels.RobotFileModel;

import java.util.*;

public class RobotsParser{
    //what should I send ???
    private static final String HOST = "^HOST:.*";
    private static final String USER_AGENT = "^User-agent:.*";
    private static final String ALLOW = "^Allow:.*";
    private static final String DISALLOW = "^Disallow:.*";
    private static final String SITE_MAP = "^Sitemap:.*";
    private static final String CRAWL_DELAY = "^Crawl-delay:.*";
    private List<URLInfo> robotsMap;

    public static void model(RobotFileModel robotFileModel,String input) {

        List<String> lines = getLines(input);
        enrichLinesIntoRobot(robotFileModel,lines);
    }

    public static void enrichLinesIntoRobot(RobotFileModel robotFileModel,List<String> lines)
    {
        List<String> allowFieldLines = null;
        List<String> disallowFieldLines = null;
        List<String> agentsList = new ArrayList<>();

        List<String> sitesMap = new ArrayList<>();
        List<String> hostsList = new ArrayList<>();


        for (int i = 0; i < lines.size(); i++) {

            if (lines.get(i).startsWith("#")) {
                continue;
            }

            if (lines.get(i).matches(USER_AGENT)) {

                String userAgentName = getValue(lines.get(i));

                if(i>0 && lines.get(i-1).matches(USER_AGENT)) {
                    agentsList.add(userAgentName);
                } else {
                    processData(robotFileModel, allowFieldLines, disallowFieldLines, agentsList);
                    agentsList = new ArrayList<>();
                    allowFieldLines = new ArrayList<>();
                    disallowFieldLines = new ArrayList<>();
                    agentsList.add(userAgentName);
                }

            } else if (lines.get(i).matches(ALLOW) && allowFieldLines != null) {
                allowFieldLines.add(getValue(lines.get(i)));

            } else if (lines.get(i).matches(DISALLOW) && disallowFieldLines != null) {
                disallowFieldLines.add(getValue(lines.get(i)));
            } else if (lines.get(i).matches(CRAWL_DELAY)) {

                for(String agent: agentsList) {
                    if (!robotFileModel.getAllowedLinks().containsKey(agent)) {
                        robotFileModel.getAllowedLinks().put(agent, new ArrayList<>());
                        robotFileModel.getDisallowedLinks().put(agent, new ArrayList<>());
                        robotFileModel.getCrawlDelays().put(agent, 1);
                    }
                    robotFileModel.getCrawlDelays().put(agent, Integer.valueOf(getValue(lines.get(i))));
                }
            } else if (lines.get(i).matches(SITE_MAP) || lines.get(i).matches(HOST)) {

                if (lines.get(i).matches(SITE_MAP)) {
                    sitesMap.add(getValue(lines.get(i)));
                } else if (lines.get(i).matches(HOST)) {
                    hostsList.add(getValue(lines.get(i)));
                }

                processData(robotFileModel, allowFieldLines, disallowFieldLines, agentsList);

                agentsList = new ArrayList<>();
                allowFieldLines = new ArrayList<>();
                disallowFieldLines = new ArrayList<>();

            }

        }

        processData(robotFileModel, allowFieldLines, disallowFieldLines, agentsList);

    }

    private static void processData(RobotFileModel robotFileModel, List<String> allowFieldLines, List<String> disallowFieldLines, List<String> agentsList) {
        for (String agent : agentsList) {

            if (!robotFileModel.getAllowedLinks().containsKey(agent)) {
                robotFileModel.getAllowedLinks().put(agent, new ArrayList<>());
                robotFileModel.getDisallowedLinks().put(agent, new ArrayList<>());
                robotFileModel.getCrawlDelays().put(agent, 1);
            }

            robotFileModel.getAllowedLinks().get(agent).addAll(allowFieldLines);
            robotFileModel.getDisallowedLinks().get(agent).addAll(disallowFieldLines);
        }
    }

    private static String getValue(String line) {
        String[] list = line.split(":", 2);
        if (list.length != 1) {
            return list[1].trim();
        }
        return "";
    }

    public static List<String> getLines(String input)
    {
        String temp = input.replaceAll("\r\n", "\n");
        String[] lines = temp.split("\n");
        return Arrays.asList(lines);
    }

    public static void main(String[] args) {
        RobotsParser robotsParser = new RobotsParser();
        //System.out.println(getValue("User-agent: cis455crawler"));
    }
}

