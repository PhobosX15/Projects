package bots.skill.template;

import bots.skill.template.question.Question;
import bots.skill.template.question.QuestionWord;
import bots.skill.template.slot.Slot;
import bots.skill.template.slot.SlotUnit;
import com.badlogic.gdx.utils.Array;

import java.util.LinkedList;
import java.util.List;

public class Skill {

    protected Question question;
    protected List<Slot> slots;
    protected List<Action> actions;

    public Skill(Question q, List<Slot> s, List<Action> a) {
        this.question = new Question(q);
        this.slots = new LinkedList<>(s);
        this.actions = new LinkedList<>(a);
    }

    public Skill(Skill skill) {
        // Copying the question
        this.question = new Question(skill.getQuestion());
        List<QuestionWord> words = new LinkedList<>();
        for (QuestionWord word : this.question.getQuestion()) {
            words.add(new QuestionWord(word));
        }
        this.question.setQuestion(words);

        // Copying the slots
        this.slots = new LinkedList<>();
        for (Slot slot : skill.getSlots()) {
            Slot newSlot = new Slot(slot.getName());
            for (SlotUnit unit : slot.getUnits()) {
                new SlotUnit(unit.getSlotUnitText(), newSlot);
            }
            this.slots.add(newSlot);
        }

        this.actions = new LinkedList<>();
        for (Action action : skill.getActions()) {
            List<SlotUnit> unitList = new LinkedList<>();
            for (SlotUnit unit : action.getSlotUnits()) {
                SlotUnit newUnit = getSlot(unit.getSlot().getName())
                        .getSlotUnit(unit.getSlotUnitText());
                unitList.add(newUnit);
            }
            this.actions.add(new Action(unitList, action.getActionText()));
        }
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Slot> getSlots() {
        return this.slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public void addSlot(Slot slot) {
        this.slots.add(slot);
    }

    public List<Action> getActions() {
        return this.actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    /**
     * Finds the slot, corresponding to the given string.
     * @param slotString The name of the slot that needs to be found.
     * @return The found slot. If it can't find it, it returns null.
     */
    public Slot getSlot(String slotString) {
        for (Slot slot : this.slots) {
            if (slot.getName().equals(slotString)) {
                return slot;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Skill skill = (Skill) o;
        return this.question.equals(skill.getQuestion()) && this.slots.equals(skill.getSlots()) &&
                this.actions.equals(skill.getActions());
    }

    @Override
    public String toString() {
        StringBuilder skillString = new StringBuilder("Skill:\nQuestion " + this.question.toString());
        for (Slot slot : this.slots) {
            skillString.append("\n").append(slot.toString());
        }
        // This seems weird because of a change that was later removed.
        skillString.append(actionsToString());
        return skillString.toString();
    }

    protected String actionsToString() {
        StringBuilder actionsString = new StringBuilder();
        for (Action action : this.actions) {
            actionsString.append("\n").append(action.toString());
        }
        return actionsString.toString();
    }

    public static List<List<SlotUnit>> getAllCombosOfAllUnits(List<Slot> slots) {
        List<List<SlotUnit>> allCombos = new LinkedList<>();
        if (slots.isEmpty()) {
            allCombos.add(new LinkedList<SlotUnit>());
            return allCombos;
        } else {
            List<Slot> newSlotList = slots.subList(1, slots.size());
            Slot firstElement = slots.get(0);
            for (SlotUnit unit : firstElement.getUnits()) {
                List<List<SlotUnit>> comboSet = getAllCombosOfAllUnits(newSlotList);
                for (List<SlotUnit> unitList : comboSet) {
                    unitList.add(unit);
                }
                allCombos.addAll(comboSet);
            }
            allCombos.addAll(getAllCombosOfAllUnits(newSlotList));
        }
        return allCombos;
    }
}
