package bots.dbInteractor;

import bots.skill.template.Action;
import bots.skill.template.Skill;
import bots.skill.template.question.Question;
import bots.skill.template.slot.Slot;
import bots.skill.template.slot.SlotUnit;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TxtInteractor extends FileInteractor {

    /**
     * @param file Needs to be a .txt file
     */
    public TxtInteractor(File file) {
        super(file);
    }

    /**
     * Saves the skill properly to the .txt file.
     */
    public void saveToFile(Skill skill) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.file, true));

            // Writing the question to the file.
            String line = "Question " + skill.getQuestion().toString();
            writeLine(writer, line);

            // Writing the slots to the file.
            for (Slot slot : skill.getSlots()) {
                writeLine(writer, slot.toString());
            }

            // Writing the actions to the file.
            for (Action action : skill.getActions()) {
                writeLine(writer, action.toString());
            }
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Scans the given file for skills.
     * Each skill needs to start with "Question" and all words should be separated by a space.
     */
    public List<Skill> convertFileToSkills() {
        List<Skill> allSkillsInFile = new LinkedList<>();

        // Converts the file into a list, removing all the empty lines.
        List<String> fileAsList = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    fileAsList.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converts the found list into skills
        List<String> skillAsStringList = new LinkedList<>();
        boolean recordingSkill = false;
        for (String line : fileAsList) {
            // If the first word is "Question" then it starts 'recording' a new skill.
            if (line.split(" ")[0].equals("Question")) {
                // If the system was already 'recording' a skill. It saves the skill it was working on.
                // And starts recording the new one.
                if (recordingSkill) {
                    allSkillsInFile.add(convertToSkill(skillAsStringList));
                    skillAsStringList = new LinkedList<>();
                }
                recordingSkill = true;
            }
            skillAsStringList.add(line);
        }
        // If the list is not empty, then a skill was skipped. This still needs to be added.
        if (!skillAsStringList.isEmpty()) {
            allSkillsInFile.add(convertToSkill(skillAsStringList));
        }
        return allSkillsInFile;
    }

    /**
     * Takes a list of strings and turns it into a Skill.
     * This programme will throw an IllegalArgumentException if the given list of strings is not correctly formatted.
     * @param newSkill Each string needs to be a line of either the question, a slot or an action.
     *                 All words should be separated by a space.
     *                 Each question string needs to start with "Question".
     *                 Each slot string needs to start with "Slot".
     *                 Each action string needs to start with "Action".
     * @throws IllegalArgumentException This will be thrown if,
     * either a slot unit in an action is not mentioned in the slots,
     * or when a slot in the question does not have any slot units in the slots.
     */
    protected Skill convertToSkill(List<String> newSkill) throws IllegalArgumentException{
        String[] questionLine = null;
        List<String[]> slotLines = new LinkedList<>();
        List<String[]> actionLines = new LinkedList<>();

        // Assigning each line to their corresponding part.
        for (String line : newSkill) {
            String[] words = line.split(" ");
            switch (words[0]){
                case "Question":
                    questionLine = words;
                    break;
                case "Slot":
                    slotLines.add(words);
                    break;
                case "Action":
                    actionLines.add(words);
                    break;
            }
        }

        // Creating the question for the skill.
        String questionString = null;
        if (questionLine != null) {
            questionString = "";
            // We start at 1 because the first word is "Question".
            int start = 1;
            for (int i = start; i < questionLine.length; i++) {
                if (i != start){
                    questionString = questionString.concat(" ");
                }
                questionString = questionString.concat(questionLine[i]);
            }
        }
        assert questionString != null;
        Question question = new Question(questionString);

        // Creating the slots of the skill.
        List<Slot> slots = new LinkedList<>();
        List<String> slotNamesInQuestion = question.getSlotNames();
        List<String> slotNames = new LinkedList<>();
        for (String[] slotLine : slotLines) {
            // Index 1 is the slot name.
            String slotName = slotLine[1];

            // If the slot name is not mentioned in the question it will throw an error.
            if (!slotNamesInQuestion.contains(slotName)) {
                throw new IllegalArgumentException("The provided slot does not appear in the question." +
                        "\nThe slot's name: " + slotName +
                        "\nThe available slots: " + slotNamesInQuestion.toString());
            }
            String slotUnitString = "";

            // Here, the program starts at index 2, because index 0 is "Slot" and index 1 is the name of the slot.
            int start = 2;
            for (int i = start; i < slotLine.length; i++) {
                if (i != start) {
                    slotUnitString = slotUnitString.concat(" ");
                }
                slotUnitString = slotUnitString.concat(slotLine[i]);
            }

            // If the slot already exists. It won't create a new one, but just add the found unit to it.
            Slot slot = slotListContainsSlot(slots, slotName);
            if (slot == null) {
                slot = new Slot(slotName);
                slotNames.add(slotName);
                slots.add(slot);
            }
            new SlotUnit(slotUnitString, slot);
        }
        // If a slot in the question does not appear in the slots. Throw an exception
        for (String questionSlot : slotNamesInQuestion) {
            if (!slotNames.contains(questionSlot)) {
                throw new IllegalArgumentException("A slot in the question is not mentioned in the list of slots." +
                        "\nThe problematic slot in the question is: " + questionSlot +
                        "\nThe list of names of slots: " + slotNames.toString());
            }
        }

        // Creating the actions of the skill.
        List<Action> actions = new LinkedList<>();
        for (String[] actionLine : actionLines) {
            String actionText = "";
            List<SlotUnit> slotUnits = new LinkedList<>();
            boolean scanningSlots = true;

            // Ignore the first word, because it's "Action"
            int start = 1;
            for (int i = start; i < actionLine.length; i++) {
                String word = actionLine[i];
                if (scanningSlots) {
                    Slot slot = slotListContainsSlot(slots, word);
                    if (slot == null) {
                        scanningSlots = false;
                    } else {
                        // Increases the iterator, because it is currently on the index of the slot's name.
                        i++;

                        // Finds the slot unit
                        List<String> remainingText = Arrays.asList(actionLine).subList(i, actionLine.length);
                        SlotUnit unit = slot.getUnit(remainingText, slot.getUnits());

                        // If the slot unit does not exist. Then an error is thrown.
                        if (unit == null) {
                            throw new IllegalArgumentException("The given slot unit in the action, " +
                                    "is not mentioned in the slots. " +
                                    "\nRemaining text after the slot: " + remainingText +
                                    "\nSlot units in the slot: " + slot.getUnits().toString());
                        }
                        slotUnits.add(unit);

                        /* Increases the iterator by the amount of words in the slot unit's text minus 1.
                         * It should increase the iterator by the amount of words.
                         * But because the for-loop already increases the iterator by 1,
                         * we subtract 1 from the amount of words. */
                        i += unit.getSlotUnitText().split(" ").length - 1;
                        continue;
                    }
                }
                // This is the text of the action, so the answer it returns with the correct question.
                if (!actionText.isEmpty()) {
                    actionText = actionText.concat(" ");
                }
                actionText = actionText.concat(word);
            }
            actions.add(new Action(slotUnits, actionText));
        }
        return new Skill(question, slots, actions);
    }

    /**
     * Writes the given line in the given writer and writes a new line.
     */
    private void writeLine(BufferedWriter writer, String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the given skill from the file.
     * @return Whether the skill has been properly removed.
     */
    public boolean removeSkillFromFile(Skill skillToRemove) {
        // Creates a new file that will be renamed to the old file.
        File tempFile = new File(this.file.getParent() + "\\tempFile.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            // Each line will be rewritten to the other file, unless it detects the skill that needs to be removed.
            boolean foundSkill = false;
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                String[] readLineWords = readLine.split(" ");
                // Finds the skill that shouldn't be written to the file.
                if (readLineWords[0].equals("Question")) {
                    foundSkill = skillToRemove.getQuestion().questionCheck(readLineWords);
                }
                // If it didn't find the skill, it'll write the line.
                if (!foundSkill) {
                    writeLine(writer, readLine);
                }
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // Replacing the old file by the new file
        boolean originalDeleted = this.file.delete();
        boolean success = false;
        if (originalDeleted) {
            success = tempFile.renameTo(this.file);
        }
        return success;
    }

    /**
     * Replaces the skill in the file by the new skill.
     * The method in this class ignores the replaceQuestion, replaceSlots, and replaceActions parameters.
     * Because the way one can replace lines in a .txt files is by replacing the entire file.
     * @return Whether the conversion has succeeded.
     */
    public boolean replaceSkillInFile(Skill oldSkill, Skill newSkill,
                                      boolean replaceQuestion, boolean replaceSlots, boolean replaceActions) {
        // Creates a new file that will be renamed to the old file.
        File tempFile = new File(this.file.getParent() + "\\tempFile.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            // Each line will be rewritten to the other file.
            // If it detects the skill that needs to be edited, then it'll write the new skill instead.
            boolean foundSkill = false;
            boolean newSkillWritten = false;
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                String[] readLineWords = readLine.split(" ");
                // Finds the skill that shouldn't be written to the file.
                if (readLineWords[0].equals("Question")) {
                    foundSkill = oldSkill.getQuestion().questionCheck(readLineWords);
                }
                // If it didn't find the skill, it'll write the line.
                // If it did and the new skill is not written yet, it'll write the new skill.
                if (!foundSkill) {
                    writeLine(writer, readLine);
                } else if (!newSkillWritten) {
                    saveToFile(newSkill);
                    newSkillWritten = true;
                }
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // Replacing the old file by the new file
        boolean originalDeleted = this.file.delete();
        boolean success = false;
        if (originalDeleted) {
            success = tempFile.renameTo(this.file);
        }
        return success;
    }
}
