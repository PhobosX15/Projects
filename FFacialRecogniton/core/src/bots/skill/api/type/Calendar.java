package bots.skill.api.type;

import bots.skill.api.slot.slotType.SlotType;

import java.util.List;
import java.util.Map;

public class Calendar extends APIType {

    public Calendar(String key) {
        super(APITypeName.CALENDAR, key);
    }

    @Override
    public String getInfo(Map<SlotType, String> typesWithInfo) {
        return "This API is a WIP";
    }
}
