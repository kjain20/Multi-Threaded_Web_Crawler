package edu.upenn.cis.cis455.Util;

public class UserAgentMatcher {

    public static boolean match(String regex, String url) {

        if (regex.charAt(regex.length() - 1) == '$') {
            url = url + '$';
        } else if(regex.charAt(regex.length() - 1) != '*') {
            regex = regex + "*";
        }


        boolean output[][] = new boolean[url.length() + 1][regex.length() + 1];

        for (int i = 0; i <= url.length(); i++) {
            output[i][0] = false;
        }

        for (int i = 1; i <= regex.length(); i++) {
            if (regex.charAt(i - 1) != '*')
                output[0][i] = false;
            else
                output[0][i] = output[0][i - 1];
        }

        output[0][0] = true;

        for (int i = 1; i <= url.length(); i++) {
            for (int j = 1; j <= regex.length(); j++) {

                if (url.charAt(i - 1) == regex.charAt(j - 1)) {
                    output[i][j] = output[i - 1][j - 1];
                } else if (regex.charAt(j - 1) == '*') {
                    output[i][j] = output[i - 1][j - 1];
                    output[i][j] = output[i][j] || output[i - 1][j];
                    output[i][j] = output[i][j] || output[i][j - 1];
                }
            }
        }
        return output[url.length()][regex.length()];
    }

}
