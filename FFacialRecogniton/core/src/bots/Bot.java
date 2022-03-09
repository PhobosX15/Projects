package bots;

import bots.skill.template.Action;
import bots.skill.template.Skill;
import bots.skill.template.question.Question;
import bots.skill.template.slot.Slot;
import bots.skill.template.slot.SlotUnit;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class Bot {

    /**
     * You ask the bot a question, the bot should give an answer.
     */
    public abstract String ask(String question) throws IOException;

    /**
     * Creates and returns the requested bot.
     */
    public static Bot createBot(BotName botName) {
        switch (botName){
            case TEMPLATE:
                return new TemplateBot();
            /*case TEXTFILE:
                return new TextfileBot();*/
            case API:
                return new APIBot();
            default:
                return null;
        }
    }

    protected Skill convertInputToSkill(Question question, List<Slot> slots, List<Action> actions) {
        Skill newSkill = new Skill(question, slots, actions);
        if (checkSkill(newSkill)) {
            return newSkill;
        } else {
            return null;
        }
    }

    /**
     * Checks if the given skill has correct parameters. If so, it'll return true
     * If not, it will throw an exception.
     * @throws IllegalArgumentException This will be thrown if,
     * either a slot unit in an action is not mentioned in the slots,
     * or when a slot in the question does not have any slot units in the list of slots.
     * @return true if it has correct parameters. If not an exception is thrown.
     */
    protected boolean checkSkill(Skill skill) throws IllegalArgumentException {
        // Saves all the names of the slots in the list of slots.
        // To later check whether the slots mentioned in the question are mentioned in the list of slots.
        // Also saves all slot units to check whether all mentioned slot units in the actions all appear in this list.
        List<String> slotNames = new LinkedList<>();
        List<SlotUnit> allUnits = new LinkedList<>();
        for (Slot slot : skill.getSlots()) {
            if (!slotNames.contains(slot.getName())) {
                slotNames.add(slot.getName());
                allUnits.addAll(slot.getUnits());
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
        // Checks whether all given slot unit in all actions appear in the list of slot units.
        for (Action action : skill.getActions()) {
            for (SlotUnit slotunit : action.getSlotUnits()) {
                if (!allUnits.contains(slotunit)) {
                    throw new IllegalArgumentException("The given slot unit in the action, " +
                            "is not mentioned in the slots. " +
                            "\nAction: " + action.toString() +
                            "\nThe problematic slot unit: " + slotunit.toString() +
                            "\nThe slots that don't contain the slot unit: " + skill.getSlots().toString());
                }
            }
        }
        // If the code reaches this point, the give parameters should be correct.
        return true;
    }

    /**
     * Finds the action for the corresponding question. If no action is found, it throws an exception.
     * @param question First word shouldn't be the "Question" keyword, in contrary to other methods.
     * @throws NullPointerException This error is thrown when either the skill cannot be found,
     * so when the question does not correspond to any skill.
     * Or it is thrown when there is no action for the given set of slots in the given question.
     */
    protected Action getAction(List<Skill> skills, String question) throws NullPointerException {
        Skill skill = null;

        // Finds the corresponding skill and saves the units it finds.
        List<SlotUnit> units = new LinkedList<>();

        for (Skill skill1 : skills) {
            Question skillQuestion = skill1.getQuestion();
            units = skillQuestion.isQuestion(skill1, question);
            if (units != null) {
                skill = skill1;
                break;
            }
        }
        // If the skill cannot be found, then an exception is thrown.
        if (skill == null) {
            throw new NullPointerException("The skill you requested cannot be found. " +
                    "Make sure you have typed your question correctly.");
        }
        /* Loops through all actions to find the best action.
        The best action contains the most non-null slot units. */
        Action bestAction = null;
        for (Action action : skill.getActions()) {
            if (action.isAction(units)) {
                if (bestAction == null) {
                    bestAction = action;
                } else if (action.nullUnits() < bestAction.nullUnits()) {
                    bestAction = action;
                } else if (action.getSlotUnits().size() > bestAction.getSlotUnits().size()) {
                    bestAction = action;
                }
            }
        }
        // If the action cannot be found, then an exception is thrown.
        if (bestAction == null) {
            throw new NullPointerException("No action was found. " +
                    "Make sure to add a default action and/or that the given slots have a corresponding action.");
        }
        return bestAction;
    }
}
