package bots;

import bots.dbInteractor.xml.XmlInteractorAPI;
import bots.skill.api.APISkill;
import bots.skill.api.slot.APISlot;
import bots.skill.api.slot.slotType.SlotType;
import bots.skill.template.Action;
import bots.skill.template.Skill;
import bots.skill.template.question.Question;
import bots.skill.template.question.QuestionWord;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class APIBot extends TemplateBot {

    private final List<APISkill> apiSkills;
    private XmlInteractorAPI fileInteractor;

    public APIBot() {
        this.skills = new LinkedList<>();
        this.apiSkills = new LinkedList<>();
        // Creating the file interactor that creates the api skills
        File file = new File("core\\src\\bots\\resources\\apiFile.xml");
        createAPIFileInteractor(file);
        setFileInteractor(this.fileInteractor);
    }

    public List<APISkill> getAPISkills() {
        return apiSkills;
    }

    protected void createAPIFileInteractor(File file) {
        try {
            this.fileInteractor = new XmlInteractorAPI(file);

            // Add the existing skills from the file to the list of skills.
            this.apiSkills.addAll(this.fileInteractor.convertFileToAPISkills());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            this.fileInteractor = null;
            e.printStackTrace();
            System.out.println("The requested file cannot be found.");
        }
    }

    @Override
    public String ask(String question) {
        // First try to find a proper api skill. If it can't be found, check the 'normal' skills.
        try {
            APISkill bestSkill = null;
            Map<SlotType, String> bestSlotInfo = new HashMap<>();

            question = Question.spacesAroundPunctuationMarks(question);
            String[] questionArray = question.split(" ");

            // Loop over all api skills to check if one of them is the skill to be used
            for (APISkill skill : this.apiSkills) {
                Map<SlotType, String> slotInfo = new HashMap<>();
                List<QuestionWord> words = skill.getQuestion().getQuestion();
                int wordsId = 0;
                int questionId = 0;

                // Check if the question is correct, since this is the identifier of the skill
                while (wordsId < words.size() && questionId < questionArray.length) {
                    QuestionWord word = words.get(wordsId);
                    if (!word.isSlot()) {
                        if (!word.getText().equalsIgnoreCase(questionArray[questionId])) {
                            break;
                        }
                    } else {
                        APISlot slot = skill.getSlot(word.getText());
                        SlotType type = slot.getType();
                        String slotText;
                        if ((wordsId + 1) >= words.size()) {
                            slotText = subArray(questionId, questionArray);
                        } else {
                            // Finds the index of the next word in the given question
                            String nextWord = words.get(wordsId + 1).getText();
                            List<String> questionList = Arrays.asList(questionArray);
                            List<String> subListQuestion = questionList.subList(questionId, questionList.size());
                            int end = subListQuestion.indexOf(nextWord);

                            // If end is -1, then the next word can't be found in the question.
                            // Therefore the next word doesn't exist in the question.
                            if (end == -1) {
                                break;
                            } else {
                                end = questionId + end;
                            }

                            // Grabs the string that is in the place of where the slot normally is.
                            slotText = subArray(questionId, end, questionArray);

                            // The - 1 comes from the fact that the question id is increased by 1
                            // at a later point in the loop
                            questionId = end - 1;
                        }
                        slotInfo.put(type, slotText);
                    }
                    wordsId++;
                    questionId++;
                }
                // This checks whether the previously mentioned while loop has completely finished.
                // If so, the question is good, therefore the skill is correct. So, info can be requested.
                if (wordsId >= words.size() && questionId >= questionArray.length && bestSlotInfo.size() < slotInfo.size()) {
                    bestSkill = skill;
                    bestSlotInfo = slotInfo;
                }
            }
            assert bestSkill != null;
            return bestSkill.getType().getInfo(bestSlotInfo);
        } catch (NullPointerException | AssertionError ignored) {
        }

        try {
            // An api skill couldn't be found. So a 'normal' skill should be found.
            Action action = getAction(this.skills, question);
            return action.getActionText();
        } catch (NullPointerException e) {
            return e.getMessage();
        }
    }

    /**
     * Creates a list from the array from start (inclusive) to end (exclusive). If the end exceeds the length,
     * then the end will be set to the length of the array.
     * @param start The first element of the subArray
     * @param end The element after the last element of the subArray
     * @return A list
     */
    public String subArray(int start, int end, String[] array) {
        StringBuilder subArray = new StringBuilder();
        if (end > array.length) {
            return subArray(start, array.length, array);
        } else {
            for (int i = start; i < end; i++) {
                if (i != start) {
                    subArray.append(" ");
                }
                subArray.append(array[i]);
            }
        }
        return subArray.toString();
    }

    /**
     * Creates a list from the array from start (inclusive) to the last element.
     * @param start The first element of the subArray
     * @return A list
     */
    public String subArray(int start, String[] array) {
        return subArray(start, array.length, array);
    }

    /**
     * Creates a new skill from the given skill
     */
    public void createNewSkill(Object o) {
        if (o instanceof Skill) {
            Skill skill = (Skill) o;
            if (checkSkill(skill)) {
                addNewSkill(skill);
            }
        } else if (o instanceof APISkill) {
            APISkill skill = (APISkill) o;
            addNewSkill(skill);
        }
    }

    /**
     * Adds new skill to the list of skills and to the file.
     */
    protected void addNewSkill(APISkill newSkill) {
        if (checkAPISkill(newSkill)) {
            if (!this.apiSkills.contains(newSkill)) {
                this.apiSkills.add(newSkill);
                this.fileInteractor.saveToFile(newSkill);
            }
        }
    }

    /**
     * Checks if the given parameters of the api skill are correctly given.
     * @param skill The skill you want to check the parameters of.
     * @return True, if it didn't throw an error.
     * @throws IllegalArgumentException When the given slotnames in the question do not appear in the api slots.
     */
    protected boolean checkAPISkill(APISkill skill) throws IllegalArgumentException {
        // Saves all the names of the slots in the list of slots.
        // To later check whether the slots mentioned in the question are mentioned in the list of slots.
        List<String> slotNames = new LinkedList<>();
        for (APISlot slot : skill.getSlots()) {
            if (!slotNames.contains(slot.getName())) {
                slotNames.add(slot.getName());
            }
        }
        // Checks whether the mentioned slots in the question appear in the list of slots.
        for (String word : skill.getQuestion().getSlotNames()) {
            if (!slotNames.contains(word)) {
                throw new IllegalArgumentException("Some given slots in the question do not appear as " +
                        "slots in the given list of slots. " +
                        "\nThis slot is not in the list of slots: " + word +
                        "\nThis is the list of slots: " + slotNames.toString());
            }
        }
        // If the code reaches this point, the give parameters should be correct.
        return true;
    }

    /**
     * Edits the skills given two objects. Calls different methods for each type of object.
     * @param oldO Should be either APISkill or Skill
     * @param newO Should be either APISkill or Skill
     * @return Whether the skills where successfully edited.
     */
    public boolean editSkill(Object oldO, Object newO) {
        if (oldO.getClass().equals(newO.getClass())) {
            if (oldO instanceof Skill) {
                Skill oldSkill = (Skill) oldO;
                Skill newSkill = (Skill) newO;
                return editSkill(oldSkill, newSkill);
            } else if (oldO instanceof APISkill) {
                APISkill oldSkill = (APISkill) oldO;
                APISkill newSkill = (APISkill) newO;
                return editSkill(oldSkill, newSkill);
            } else {
                return false;
            }
        } else if ((oldO instanceof Skill && newO instanceof APISkill) || (oldO instanceof APISkill && newO instanceof Skill)) {
            removeSkill(oldO);
            createNewSkill(newO);
        }
        return false;
    }

    /**
     * Checks param's of skill in this method
     * @param skillToEdit old skill
     * @param newSkill new skill
     * @return Whether the conversion was succesfull.
     */
    protected boolean editSkill(Skill skillToEdit, Skill newSkill) {
        if (checkSkill(newSkill)) {
            try {
                return convertSkillToNewSkill(skillToEdit, newSkill);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * This method changes the requested skill into a new skill. This change is also represented in the file.
     * If it's not possible, due to thrown errors.
     * The method will return false, indicating that it was not possible to change the skill with the given parameters.
     * If this happens, the requested skill will be unchanged.
     * @return True, if the skill was able to change. False, if the skill was not able to change.
     */
    protected boolean editAPISkill(APISkill skillToEdit, APISkill newSkill) {
        try {
            if (!checkAPISkill(newSkill)) {
                return false;
            }
            return convertSkillToNewSkill(skillToEdit, newSkill);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This changes the given skill into the other given skill. This change is also reflected in the file.
     * If the list of skills does not contain the skill to be changed, then the change will not be made.
     * And the method will return false. If the list does contain it, the conversion will be made, and return true.
     * @return Whether the list of skills contains the old skill and if the old skill is replaced.
     */
    private boolean convertSkillToNewSkill(APISkill oldSkill, APISkill newSkill) {
        if (!this.apiSkills.contains(oldSkill)) {
            return false;
        }
        // If the question is the same don't replace them.
        boolean replaceQuestion = !oldSkill.getQuestion().equals(newSkill.getQuestion());
        // If the slots are the same don't replace them.
        boolean replaceSlots = !oldSkill.getSlots().equals(newSkill.getSlots());

        // Edits the file and the list of skills.
        boolean fileEdited = this.fileInteractor.replaceSkillInFile(oldSkill, newSkill,
                replaceQuestion, replaceSlots);
        boolean listEdited = Collections.replaceAll(this.apiSkills, oldSkill, newSkill);

        // If both are successfully edited, return true.
        return fileEdited && listEdited;
    }

    /**
     * Removes the skill from the file and the list of skills.
     * @param skill Should be either Skill or APISkill
     * @return Whether the skill was successfully removed.
     */
    public boolean removeSkill(Object skill) {
        if (skill instanceof Skill) {
            Skill skillToRemove = (Skill) skill;
            return super.removeSkill(skillToRemove);
        } else if (skill instanceof APISkill) {
            APISkill apiSkill = (APISkill) skill;
            return removeAPISkill(apiSkill);
        }
        return false;
    }

    /**
     * Removes the given skill from the list of skills and from the text file.
     */
    protected boolean removeAPISkill(APISkill skillToRemove) {
        this.apiSkills.remove(skillToRemove);
        return this.fileInteractor.removeSkillFromFile(skillToRemove);
    }

    /**
     * @return Whether all skills were successfully removed.
     */
    @Override
    public boolean removeAllSkills() {
        return removeAllTemplateSkill() && removeAllAPISkills();
    }

    /**
     * @return Whether all template skills were successfully removed.
     */
    public boolean removeAllTemplateSkill() {
        return super.removeAllSkills();
    }

    /**
     * @return Whether all API skills were successfully removed.
     */
    public boolean removeAllAPISkills() {
        boolean success = true;
        for (APISkill skill : this.getAPISkills()) {
            success = success && this.removeAPISkill(skill);
        }
        return success;
    }
}
