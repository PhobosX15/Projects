package bots.skill.api.type;

import bots.skill.api.slot.slotType.SlotType;
import bots.skill.api.type.weather.Weather;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class APIType {

    protected APITypeName name;
    protected String key;

    protected APIType(APITypeName name, String key) {
        this.name = name;
        this.key = key;
    }

    public static APIType getInstance(APITypeName typeName, String key) {
        if (typeName == APITypeName.WEATHER) {
            return new Weather(key);
        } else {
            return new Calendar(key);
        }
    }

    public static APIType getInstance(String typeName, String key) {
        return getInstance(getNameFromString(typeName), key);
    }

    /**
     * Info is requested.
     * @param typesWithInfo A map where the keys are slot types.
     *                      And the values are the strings that are in the place of the slots
     * @return The requested info.
     */
    public abstract String getInfo(Map<SlotType, String> typesWithInfo);

    public APITypeName getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public static APITypeName getNameFromString(String name) {
        for (APITypeName typeName : APITypeName.values()) {
            if (name.equalsIgnoreCase(typeName.name())) {
                return typeName;
            }
        }
        return APITypeName.WEATHER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        APIType apiType = (APIType) o;
        return this.name == apiType.getName() && Objects.equals(this.key, apiType.getKey());
    }
}
