package bots.skill.api.slot.slotType.types.day;

import bots.skill.api.slot.slotType.SlotType;
import bots.skill.api.slot.slotType.SlotTypeName;

import java.util.LinkedList;
import java.util.List;

public class Day extends SlotType {

    private final List<String> days;

    public Day(SlotTypeName name) {
        super(name);
        this.days = new LinkedList<>();
        for (DayName dayName : DayName.values()) {
            this.days.add(dayName.name());
        }
    }

    @Override
    public String[] checkFormat(String request) {
        for (String day : this.days) {
            if (day.equalsIgnoreCase(request)) {
                return new String[]{day};
            }
        }
        return new String[]{""};
    }
}
