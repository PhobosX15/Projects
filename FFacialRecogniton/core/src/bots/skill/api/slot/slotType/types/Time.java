package bots.skill.api.slot.slotType.types;

import bots.skill.api.slot.slotType.SlotType;
import bots.skill.api.slot.slotType.SlotTypeName;

public class Time extends SlotType {

    public static final String[] TIME_SPLIT = new String[]{":", "\\.", " "};

    public Time(SlotTypeName name) {
        super(name);
    }

    @Override
    public String[] checkFormat(String request) {
        boolean success;

        try {
            return new String[]{String.valueOf(Integer.parseInt(request))};
        } catch (NumberFormatException ignored) {
            success = false;
        }

        String[] requestArray;
        for (String splitter : TIME_SPLIT) {
            requestArray = request.split(splitter);
            if (requestArray.length == 2) {
                success = true;
                for (int i = 0; i < requestArray.length; i++) {
                    try {
                        int number = Integer.parseInt(requestArray[i]);
                        if (i == 0) {
                            if (!isHour(number)) {
                                success = false;
                                break;
                            }
                        } else {
                            if (!isMinute(number)) {
                                success = false;
                                break;
                            }
                        }
                    } catch (NumberFormatException e) {
                        success = false;
                        break;
                    }
                }
                for (String requestArrayElement : requestArray) {
                    try {
                        Integer.parseInt(requestArrayElement);
                    } catch (NumberFormatException e) {
                        success = false;
                        break;
                    }
                }
            }
            if (success) {
                return requestArray;
            }
        }
        return new String[]{""};
    }

    /**
     * Returns true if the number is between 0 (inclusive) and 24 (exclusive)
     */
    public static boolean isHour(int number) {
        return 0 <= number && number < 24;
    }

    /**
     * Returns true if the number is between 0 (inclusive) and 60 (exclusive)
     */
    public static boolean isMinute(int number) {
        return 0 <= number && number < 60;
    }
}
