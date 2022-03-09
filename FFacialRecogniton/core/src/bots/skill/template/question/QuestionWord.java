package bots.skill.template.question;

public class QuestionWord {
    protected final String text;
    protected boolean slot;

    public QuestionWord(String wordText){
        this.text = wordText;
        this.slot = false;
    }

    public QuestionWord(QuestionWord word) {
        this.text = word.getText();
        this.slot = word.isSlot();
    }

    public String getText() {
        return this.text;
    }

    public boolean isSlot() {
        return this.slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuestionWord that = (QuestionWord) o;
        return this.slot == that.isSlot() && this.text.equalsIgnoreCase(that.getText());
    }
}
