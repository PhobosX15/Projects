package bots.skill.api;

import bots.skill.template.question.Question;
import bots.skill.template.question.QuestionSlot;
import bots.skill.template.question.QuestionWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class APIQuestion {
    private final List<QuestionWord> question;

    public APIQuestion(String questionString) {
        this.question = new ArrayList<>();
        questionString = Question.spacesAroundPunctuationMarks(questionString);

        // Converts the given string into the proper type of words.
        for (String wordString : questionString.split(" ")) {
            QuestionWord word;
            if (Question.slotTest(wordString)) {
                word = new QuestionSlot(wordString);
            } else {
                word = new QuestionWord(wordString);
            }
            question.add(word);
        }
    }

    public List<QuestionWord> getQuestion() {
        return this.question;
    }

    /**
     * Loops through the list of words and filters out the non-slots.
     */
    public List<QuestionWord> getSlots() {
        return Question.getSlots(this.question);
    }

    /**
     * Loops through the slots in the list of words and finds the names of them.
     */
    public List<String> getSlotNames() {
        return Question.getSlotNames(this.question);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        APIQuestion that = (APIQuestion) o;
        return Objects.equals(this.question, that.question);
    }

    /**
     * Is {@link Question#questionToString(List)}
     */
    @Override
    public String toString() {
        return Question.questionToString(this.question);
    }
}
