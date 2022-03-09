package bots.skill.api;

import bots.skill.api.slot.APISlot;
import bots.skill.api.type.APIType;

import java.util.List;
import java.util.Objects;

public class APISkill {

    private final APIQuestion question;
    private final List<APISlot> slots;
    private final APIType type;

    public APISkill(APIQuestion question, List<APISlot> apiKeys, APIType type) {
        this.question = question;
        this.slots = apiKeys;
        this.type = type;
    }

    public APIQuestion getQuestion() {
        return this.question;
    }

    public List<APISlot> getSlots() {
        return this.slots;
    }

    public APIType getType() {
        return this.type;
    }

    /**
     * Finds the slot with the given name.
     * @param slotName The name of the slot
     * @return The found slot. If it can't be found, it returns null.
     */
    public APISlot getSlot(String slotName) {
        for (APISlot slot : this.slots) {
            if (slot.getName().equals(slotName)) {
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
        APISkill apiSkill = (APISkill) o;
        return Objects.equals(this.question, apiSkill.getQuestion()) &&
                Objects.equals(this.slots, apiSkill.getSlots()) && Objects.equals(this.type, apiSkill.getType());
    }

    @Override
    public String toString() {
        return "APISkill{" +
                ", question=" + this.question +
                ", slots=" + this.slots +
                ", type=" + this.type +
                '}';
    }
}
