package bots.skill.text;

import java.util.ArrayList;
import java.util.List;

public class textskill {
    List<String> keywords;
    List<List<String>> answers;
    String file;
    public textskill(String file,List<String> keywords, List<List<String>> answers)
    {
        this.file= file;
        this.keywords=keywords;
        this.answers= answers;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getKeywords()
    {
        return  keywords;
    }
    public List<List<String>> getAnswers()
    {
        return answers;
    }

    public void setAnswers(List<List<String>> answers) {
        this.answers = answers;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
