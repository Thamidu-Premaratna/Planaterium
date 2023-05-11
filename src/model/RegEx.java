package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegEx {

    protected String regex_pattern;
    protected String regex_input;

    RegEx(String pattern, String input) {
        this.regex_pattern = pattern;
        this.regex_input = input;
    }

    //Takes the argument "input to be checked"
    public boolean validateRegex() {
        Pattern pattern = Pattern.compile(this.regex_pattern);
        Matcher matcher = pattern.matcher(this.regex_input);
        return matcher.matches();
    }

    public void setPattern(String pattern) {
        this.regex_pattern = pattern;
    }

    public String getPattern() {
        return this.regex_pattern;
    }

    public void setInput(String input) {
        this.regex_input = input;
    }

    public String getInput() {
        return this.regex_input;
    }
}
