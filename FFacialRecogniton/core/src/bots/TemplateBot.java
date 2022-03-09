package bots;

import bots.dbInteractor.FileInteractor;
import bots.dbInteractor.FileInteractorName;
import bots.skill.template.Action;
import bots.skill.template.Skill;
import bots.skill.template.question.Question;
import bots.skill.template.slot.Slot;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TemplateBot extends Bot {

    protected List<Skill> skills;
    private FileInteractor fileInteractor;

    /**
     * Constructor. This initialises the instance variables
     */
    public TemplateBot() {
        this.skills = new LinkedList<>();
        FileInteractorName fileInteractorName = FileInteractorName.XML;
        File file = new File("core\\src\\bots\\resources\\templateFile.xml");
        createFileInteractor(file, fileInteractorName);
    }

    protected void createFileInteractor(File file, FileInteractorName name) {
        try {
            this.fileInteractor = FileInteractor.getInstance(name, file);

            // Add the existing skills from the file to the list of skills.
            assert this.fileInteractor != null;
            this.skills.addAll(this.fileInteractor.convertFileToSkills());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            this.fileInteractor = null;
            e.printStackTrace();
            System.out.println("The requested file cannot be found.");
        }
    }

    @Override
    public String ask(String question) {
        String answer;
        // Tries to get the correct action.
        try {
            Action action = getAction(this.skills, question);
            answer = action.getActionText();
        } // If the programme can't find an action. The error is given instead.
        catch (NullPointerException e) {
            answer = e.getMessage();
        }
        return answer;
    }

    /**
     * Converts the given parameters to a skill.
     * If the given parameters are incorrect it will throw an IllegalArgumentException.
     */
    public void createNewSkill(Question question, List<Slot> slots, List<Action> actions) {
        addNewSkill(convertInputToSkill(question, slots, actions));
    }

    /**
     * Adds the found skill to the list of skills and saves it to the .txt file.
     */
    protected void addNewSkill(Skill newSkill){
        if (!this.skills.contains(newSkill)) {
            this.skills.add(newSkill);
            this.fileInteractor.saveToFile(newSkill);
        }
    }

    /**
     * Removes the given skill from the list of skills and from the text file.
     */
    public boolean removeSkill(Skill skillToRemove) {
        this.skills.remove(skillToRemove);
        return this.fileInteractor.removeSkillFromFile(skillToRemove);
    }

    /**
     * This method changes the requested skill into a new skill. This change is also represented in the file.
     * If it's not possible, due to thrown errors.
     * The method will return false, indicating that it was not possible to change the skill with the given parameters.
     * If this happens, the requested skill will be unchanged.
     * @return True, if the skill was able to change. False, if the skill was not able to change.
     */
    public boolean editSkill(Skill skillToEdit, Question newQuestion, List<Slot> newSlots,
                             List<Action> newActions) {
        Skill newSkill;
        try {
            newSkill = convertInputToSkill(newQuestion, newSlots, newActions);
            return convertSkillToNewSkill(skillToEdit, newSkill);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * This changes the given skill into the other given skill. This change is also reflected in the file.
     * If the list of skills does not contain the skill to be changed, then the change will not be made.
     * And the method will return false. If the list does contain it, the conversion will be made, and return true.
     * @return Whether the list of skills contains the old skill and if the old skill is replaced.
     */
    protected boolean convertSkillToNewSkill(Skill oldSkill, Skill newSkill) {
        if (!this.skills.contains(oldSkill)) {
            return false;
        }
        // If the question is the same don't replace them.
        boolean replaceQuestion = !oldSkill.getQuestion().equals(newSkill.getQuestion());
        // If the slots are the same don't replace them.
        boolean replaceSlots = !oldSkill.getSlots().equals(newSkill.getSlots());
        // If the actions are the same don't replace them.
        boolean replaceActions = !oldSkill.getActions().equals(newSkill.getActions());

        // Edits the file and the list of skills.
        boolean fileEdited = this.fileInteractor.replaceSkillInFile(oldSkill, newSkill,
                replaceQuestion, replaceSlots, replaceActions);
        boolean listEdited = Collections.replaceAll(this.skills, oldSkill, newSkill);

        // If both are successfully edited, return true.
        return fileEdited && listEdited;
    }

    public List<Skill> getSkills() {
        return this.skills;
    }

    /**
     * Removes all skills from the file and the list of skills.
     * @return Whether it was able to remove all the skills.
     */
    public boolean removeAllSkills() {
        boolean success = true;
        for (Skill skill : this.getSkills()) {
            success = success && this.removeSkill(skill);
        }
        return success;
    }

    public void setFileInteractor(FileInteractor fileInteractor) {
        this.fileInteractor = fileInteractor;
        this.skills.addAll(this.fileInteractor.convertFileToSkills());
    }
}
