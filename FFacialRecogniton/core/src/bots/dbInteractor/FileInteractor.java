package bots.dbInteractor;

import bots.dbInteractor.xml.XmlInteractor;
import bots.dbInteractor.xml.XmlInteractorAPI;
import bots.skill.template.Skill;
import bots.skill.template.slot.Slot;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class FileInteractor {
    protected final File file;

    protected FileInteractor(File file) {
        this.file = file;
    }

    /**
     * This will get the correct interactor given the interactor name. By default this will return the txt interactor.
     */
    public static FileInteractor getInstance(FileInteractorName name, File file)
            throws IOException, SAXException, ParserConfigurationException {
        switch (name) {
            case TXT:
                return new TxtInteractor(file);
            case XML:
                return new XmlInteractor(file);
            case XMLAPI:
                return new XmlInteractorAPI(file);
            default:
                return null;
        }
    }

    /**
     * Saves the given skill to the file, which is an instance variable.
     */
    public abstract void saveToFile(Skill skill);

    /**
     * Converts the file (instance variable), to a list of skills.
     */
    public abstract List<Skill> convertFileToSkills();

    /**
     * Removes the specified skill that should be removed from the file.
     * @return Whether the skill was successfully removed.
     */
    public abstract boolean removeSkillFromFile(Skill skillToRemove);

    /**
     * Replaces the old skill with the new skill.
     * @param oldSkill This skill should be replaced.
     * @param newSkill This skill should replace the other skill.
     * @param replaceQuestion Indicates whether the question should be replaced.
     * @param replaceSlots Indicates whether the slots should be replaced.
     * @param replaceActions Indicates whether the actions should be replaced.
     * @return Whether the skill was successfully replaced.
     */
    public abstract boolean replaceSkillInFile(Skill oldSkill, Skill newSkill,
                                               boolean replaceQuestion, boolean replaceSlots, boolean replaceActions);

    /**
     * Finds the slot with the given name. Returns null if it's not found.
     */
    protected Slot slotListContainsSlot(List<Slot> slotList, String slotName) {
        for (Slot slot : slotList) {
            if (slot.getName().equals(slotName)){
                return slot;
            }
        }
        return null;
    }
}
