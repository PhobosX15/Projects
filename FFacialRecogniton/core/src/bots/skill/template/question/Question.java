package bots.skill.template.question;

import bots.skill.template.Skill;
import bots.skill.template.slot.Slot;
import bots.skill.template.slot.SlotUnit;

import java.util.*;

public class Question {
    private List<QuestionWord> question;

    // Some strings have "\\" in front. That's because these are reserved characters for regular expressions.
    public static final String[] PUNCTUATIONS = new String[]{",", "\\.", "!", "\\?"};

    public Question(String questionString){
        this.question = new ArrayList<>();
        questionString = spacesAroundPunctuationMarks(questionString);

        // Converts the given string into the proper type of words.
        for (String wordString : questionString.split(" ")) {
            QuestionWord word;
            if (slotTest(wordString)) {
                word = new QuestionSlot(wordString);
            } else {
                word = new QuestionWord(wordString);
            }
            question.add(word);
        }
    }

    public Question(Question question) {
        this.question = new LinkedList<>(question.question);
    }

    public List<QuestionWord> getQuestion() {
        return question;
    }

    public void setQuestion(List<QuestionWord> question) {
        this.question = question;
    }

    /**
     * Tests if the given string starts with '<' and ends with '>'.
     */
    public static boolean slotTest(String word){
        if (word.isEmpty()) {
            return false;
        }
        return word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>';
    }

    /**
     * Is {@link Question#questionToString(List)}
     */
    @Override
    public String toString() {
        return questionToString(this.question);
    }

    /**
     * Puts a space between every given word.
     */
    public static String questionToString(List<QuestionWord> words) {
        String questionString = "";
        for (int i = 0; i < words.size(); i++) {
            if (i != 0) {
                questionString = questionString.concat(" ");
            }
            questionString = questionString.concat(words.get(i).getText());
        }
        return questionString;
    }

    /**
     * Checks if the given text corresponds to this question, and returns the found slot units.
     * @return The list of slot units. If it can't find the question it returns null.
     */
    public List<SlotUnit> isQuestion(Skill skill, String text) {
        // First puts spaces around the punctuation marks to check it properly.
        text = spacesAroundPunctuationMarks(text);
        String[] textArray = text.split(" ");
        List<SlotUnit> units = new LinkedList<>();

        // Checks if the words, apart from the slots, are the same.
        int questionIterator = 0;
        int textArrayIterator = 0;
        while (questionIterator < this.question.size() && textArrayIterator < textArray.length) {
            // If the word isn't a slot, simply check the word without checking for capitalisation.
            if (!this.question.get(questionIterator).isSlot()) {
                if (!textArray[textArrayIterator].equalsIgnoreCase(this.question.get(questionIterator).getText())) {
                    return null;
                }
            }  // If it is a slot, try to find the unit from the question in the units of the skill.
            else {
                Slot skillSlot = skill.getSlot(this.question.get(questionIterator).getText());
                List<String> remainingText = Arrays.asList(textArray).subList(textArrayIterator, textArray.length);
                SlotUnit slotUnit = skillSlot.getUnit(remainingText, skillSlot.getUnits());
                if (slotUnit == null) {
                    return null;
                } else {
                    /* Increases the iterator by the amount of words in the slot unit's text minus 1.
                     * It should increase the iterator by the amount of words.
                     * But because the iterator already increases by 1 later on,
                     * we subtract 1 from the amount of words. */
                    textArrayIterator += slotUnit.getSlotUnitText().split(" ").length - 1;
                }
                units.add(slotUnit);
            }
            questionIterator++;
            textArrayIterator++;
        }
        return units;
    }

    /**
     * Loops through the list of words and filters out the non-slots.
     */
    public List<QuestionWord> getSlots() {
        return getSlots(this.question);
    }

    /**
     * Loops through the slots in the list of words and finds the names of them.
     */
    public List<String> getSlotNames() {
        return getSlotNames(this.question);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question1 = (Question) o;
        return this.question.equals(question1.question);
    }

    /**
     * Tests if the given String or String array is the same.
     * If the given object is null, it returns false.
     * If the given object is a String, it converts it to this type and checks if it's correct.
     * If, however, the first word is "Question" it converts it to a string array and checks that (see next line)
     * If the given object is a string array, it skips the first index if the keyword is "Question".
     * It builds the string from the array, to convert it to this type and checks if it's correct.
     *
     * This could be put in the equals method.
     * But putting a String or String array in an equals method makes the IDE complain.
     */
    public boolean questionCheck(Object o) {
        if (o != null) {
            if (o.getClass().equals(String.class)) {
                String questionString = (String) o;
                String[] questionStringArray = questionString.split(" ");
                if (questionStringArray[0].equals("Question")) {
                    o = questionStringArray;
                } else {
                    Question question1 = new Question(questionString);
                    return this.question.equals(question1.question);
                }
            }
            if (o.getClass().equals(String[].class)) {
                String[] questionArray = (String[]) o;
                int start = 0;
                if (questionArray[0].equals("Question")) {
                    // We start at 1 because the first word is "Question".
                    start = 1;
                }
                String questionString = "";
                for (int i = start; i < questionArray.length; i++) {
                    if (i != start) {
                        questionString = questionString.concat(" ");
                    }
                    questionString = questionString.concat(questionArray[i]);
                }
                Question question1 = new Question(questionString);
                return this.question.equals(question1.question);
            }
        }
        return false;
    }

    /**
     * Puts a space before each of the punctuation marks that are specified as an instance variable,
     * if the spaces aren't there already.
     * @return The changed string.
     */
    public static String spacesAroundPunctuationMarks(String input) {
        for (String punctuationMark : PUNCTUATIONS) {
            // Removing all spaces before the character if there are any. And adding them back.
            input = input.replaceAll(" " + punctuationMark, punctuationMark);

            // Removing all spaces after the character if there are any. And adding them back
            input = input.replaceAll(punctuationMark + " ", punctuationMark);

            // Adding the spaces back.
            input = input.replaceAll(punctuationMark, " " + punctuationMark + " ");
        }
        return input;
    }

    /**
     * Loops through the given list of words and filters out the non-slots.
     */
    public static List<QuestionWord> getSlots(List<QuestionWord> question) {
        List<QuestionWord> slots = new LinkedList<>();
        for (QuestionWord word : question) {
            if (word.isSlot()) {
                slots.add(word);
            }
        }
        return slots;
    }

    /**
     * Loops through the slots in the list of words and finds the names of them.
     */
    public static List<String> getSlotNames(List<QuestionWord> question) {
        List<String> slotNames = new LinkedList<>();
        for (QuestionWord word : getSlots(question)) {
            slotNames.add(word.getText());
        }
        return slotNames;
    }
}
