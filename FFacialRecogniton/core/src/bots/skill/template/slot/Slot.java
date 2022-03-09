package bots.skill.template.slot;

import java.util.LinkedList;
import java.util.List;

public class Slot {
    protected String slotName;
    protected List<SlotUnit> slotUnits;

    public Slot(String slotName){
        this.slotName = slotName;
        this.slotUnits = new LinkedList<>();
    }

    public Slot(Slot slot) {
        this.slotName = slot.getName();
        this.slotUnits = new LinkedList<>(slot.getUnits());
    }

    public String getName(){
        return this.slotName;
    }

    public List<SlotUnit> getUnits(){
        return this.slotUnits;
    }

    public void addUnit(SlotUnit slotUnit) {
        if (!this.slotUnits.contains(slotUnit)) {
            this.slotUnits.add(slotUnit);
        }
    }

    /**
     * Checks if the first strings of the list form a slot unit.
     * @return The correct slot unit. If it can't find it, null.
     */
    public SlotUnit getUnit(List<String> remainingText, List<SlotUnit> slots) {
        for (SlotUnit unit : slots) {
            String[] unitArray = unit.getSlotUnitText().split(" ");
            // If there are less words in the text then the amount of words in the slot unit.
            // The unit is definitely not in the text.
            if (remainingText.size() < unitArray.length) {
                continue;
            }
            boolean skip = false;
            // Checks for the amount of words of the slot unit if it is equal to the same index of the remaining text.
            // If it isn't it will check the next slot unit.
            for (int i = 0; i < unitArray.length; i++) {
                if (!unitArray[i].equalsIgnoreCase(remainingText.get(i))) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }
            return unit;
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
        Slot slot = (Slot) o;
        // The slots are equal when the names are the same.
        return this.slotName.equals(slot.getName()) && this.slotUnits.equals(slot.getUnits());
    }

    @Override
    public String toString() {
        StringBuilder slotString = new StringBuilder();
        for (int i = 0; i < this.slotUnits.size(); i++) {
            if (i != 0) {
                slotString.append("\n");
            }
            slotString.append("Slot ").append(this.slotUnits.get(i).toString());
        }
        return slotString.toString();
    }

    /**
     * Finds the slot unit with the given text from the instance variable of list of slot units.
     * If it can't find it, it returns null.
     */
    public SlotUnit getSlotUnit(String slotUnitText) {
        for (SlotUnit unit : this.slotUnits) {
            if (unit.getSlotUnitText().equals(slotUnitText)) {
                return unit;
            }
        }
        return null;
    }
}
