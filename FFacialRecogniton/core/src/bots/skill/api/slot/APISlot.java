package bots.skill.api.slot;

import bots.skill.api.slot.slotType.SlotType;

import java.util.Objects;

public class APISlot {

    private final String slotName;
    private final SlotType type;

    public APISlot(String slotName, SlotType type) {
        this.slotName = slotName;
        this.type = type;
    }

    public String getName() {
        return this.slotName;
    }

    public SlotType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "APISlot " +
                "slotName = '" + this.slotName + '\'' +
                ", apiType = '" + this.type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        APISlot apiSlot = (APISlot) o;
        return Objects.equals(this.slotName, apiSlot.getName()) && Objects.equals(this.type, apiSlot.getType());
    }
}
