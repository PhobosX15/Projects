package bots.skill.template.question;

public class QuestionSlot extends QuestionWord{

    // Only changes whether it's a slot.
    public QuestionSlot(String wordText) {
        super(wordText);
        this.slot = true;
    }

    /**
     * Separate equals method for slot, since this shouldn't and doesn't ignore capitalisation.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuestionWord that = (QuestionWord) o;
        return this.slot == that.isSlot() && this.text.equals(that.getText());
    }
}
