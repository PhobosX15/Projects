package bots.skill.api.slot.slotType.types;

import bots.skill.api.slot.slotType.SlotType;
import bots.skill.api.slot.slotType.SlotTypeName;

public class WeatherPlace extends SlotType {

    public WeatherPlace(SlotTypeName name) {
        super(name);
    }

    /**
     * This is always true, because it is too hard to check currently.
     */
    @Override
    public String[] checkFormat(String request) {
        return new String[]{request};
    }
}
