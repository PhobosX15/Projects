package bots.skill.template.slot;

public class SlotUnit {
    private String slotUnitText;
    private Slot slot;

    public SlotUnit(String unitText, Slot parent){
        this.slotUnitText = unitText;
        this.slot = parent;
        this.slot.addUnit(this);
    }

    public SlotUnit(SlotUnit unit) {
        this.slotUnitText = unit.getSlotUnitText();
        this.slot = new Slot(unit.getSlot());
    }

    public String getSlotUnitText() {
        return this.slotUnitText;
    }

    public void setSlotUnitText(String slotUnitText) {
        this.slotUnitText = slotUnitText;
    }

    public Slot getSlot() {
        return this.slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SlotUnit slotUnit = (SlotUnit) o;
        // The given object is equal when the unit text (ignoring capitalisation)
        // and the corresponding slot are the same.
        return this.slotUnitText.equalsIgnoreCase(slotUnit.getSlotUnitText()) &&
                this.slot.getName().equals(slotUnit.getSlot().getName());
    }

    @Override
    public String toString() {
        return this.slot.getName() + " " + this.slotUnitText;
    }
}
