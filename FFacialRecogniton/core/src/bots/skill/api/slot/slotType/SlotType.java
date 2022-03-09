package bots.skill.api.slot.slotType;

import bots.skill.api.slot.slotType.types.Date;
import bots.skill.api.slot.slotType.types.day.Day;
import bots.skill.api.slot.slotType.types.Time;
import bots.skill.api.slot.slotType.types.WeatherPlace;

import java.util.Objects;

public abstract class SlotType {

    private final SlotTypeName name;

    public SlotType(SlotTypeName name) {
        this.name = name;
    }

    /**
     * Gets the correct api type given the name.
     */
    public static SlotType getInstance(SlotTypeName name) {
        switch (name) {
            case DAY:
                return new Day(name);
            case DATE:
                return new Date(name);
            case TIME:
                return new Time(name);
            case PLACE:
                return new WeatherPlace(name);
            default:
                return null;
        }
    }

    /**
     * Gets the instance given the string.
     * @param name String that should be equal in name to the TypeName
     */
    public static SlotType getInstance(String name) {
        return getInstance(findTypeName(name));
    }

    /**
     * Finds the typename spelled the same as the given string. Ignores case.
     * If it doesn't exist then {@link SlotTypeName#PLACE} is returned.
     * @param name The string that should be equal to a typename
     * @return The typename spelled the same (ignoring capitalisation) as the given string.
     * If none are matched, then {@link SlotTypeName#PLACE} is returned.
     */
    protected static SlotTypeName findTypeName(String name) {
        for (SlotTypeName SlotTypeName : SlotTypeName.values()) {
            if (SlotTypeName.name().equalsIgnoreCase(name)) {
                return SlotTypeName;
            }
        }
        return SlotTypeName.PLACE;
    }

    public SlotTypeName getName() {
        return this.name;
    }

    /**
     * Checks if the given string is correctly formatted.
     * This should be overridden by each class that inherits this one. Since each one will probably behave differently.
     * @return An array of strings. If formatted incorrectly. It will return an empty array of strings
     */
    public abstract String[] checkFormat(String request);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SlotType slotType = (SlotType) o;
        return this.name == slotType.getName();
    }
}
