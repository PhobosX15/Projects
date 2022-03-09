package bots.skill.template;

import bots.skill.template.slot.SlotUnit;

import java.util.LinkedList;
import java.util.List;

public class Action {
    private final List<SlotUnit> slotUnits;
    private String action;

    public Action(List<SlotUnit> slotUnits, String action){
        this.slotUnits = slotUnits;
        this.action = action;
    }

    public Action(Action action) {
        this.slotUnits = new LinkedList<>(action.getSlotUnits());
        this.action = action.getActionText();
    }

    public List<SlotUnit> getSlotUnits() {
        return this.slotUnits;
    }

    public String getActionText() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Checks if the given slot units correspond to this action.
     */
    public boolean isAction(List<SlotUnit> units) {
        for (SlotUnit slotUnit : this.slotUnits) {
            if (slotUnit != null && !units.contains(slotUnit)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the amount of times null occurs in the slot units.
     */
    public int nullUnits() {
        int counter = 0;
        for (SlotUnit slotUnit : this.slotUnits) {
            if (slotUnit == null) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public String toString() {
        StringBuilder actionString = new StringBuilder("Action ");
        for (SlotUnit slotUnit : this.slotUnits) {
            actionString.append(slotUnit.getSlot().getName()).append(" ")
                    .append(slotUnit.getSlotUnitText()).append(" ");
        }
        actionString.append(this.action);
        return actionString.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Action action1 = (Action) o;
        return this.slotUnits.equals(action1.getSlotUnits()) && this.action.equalsIgnoreCase(action1.getActionText());
    }
}
