package bots.skill.api.slot.slotType.types;

import bots.skill.api.slot.slotType.SlotType;
import bots.skill.api.slot.slotType.SlotTypeName;

import java.util.HashMap;
import java.util.Map;

public class Date extends SlotType {

    public static final String[] DATE_SPLIT = new String[]{"\\.", "/", "-", " "};
    private final Map<Integer, Integer> maxDaysOfMonth;

    public Date(SlotTypeName name) {
        super(name);
        this.maxDaysOfMonth = new HashMap<>();
        this.maxDaysOfMonth.put(1, 31);
        this.maxDaysOfMonth.put(2, 28);
        this.maxDaysOfMonth.put(3, 31);
        this.maxDaysOfMonth.put(4, 30);
        this.maxDaysOfMonth.put(5, 31);
        this.maxDaysOfMonth.put(6, 30);
        this.maxDaysOfMonth.put(7, 31);
        this.maxDaysOfMonth.put(8, 31);
        this.maxDaysOfMonth.put(9, 30);
        this.maxDaysOfMonth.put(10, 31);
        this.maxDaysOfMonth.put(11, 30);
        this.maxDaysOfMonth.put(12, 31);
    }

    @Override
    public String[] checkFormat(String request) {
        boolean success;

        // If there is a single number, then assume it is the number of the day. Return that.
        try {
            return new String[]{String.valueOf(Integer.parseInt(request))};
        } catch (NumberFormatException ignored) {
            success = false;
        }

        String[] requestArray;
        for (String splitter : DATE_SPLIT) {
            requestArray = request.split(splitter);
            // If the array has length of two then it's DD-MM. If 3, then DD-MM-YYYY
            if (requestArray.length == 2 || requestArray.length == 3) {
                success = true;
                int month = 1;
                boolean stopLoop = false;
                for (int i = requestArray.length - 1;  i >= 0 && !stopLoop; i--) {
                    try {
                        int number = Integer.parseInt(requestArray[i]);
                        switch (i) {
                            case 0:
                                if (!isDay(number, month)) {
                                    success = false;
                                    stopLoop = true;
                                    break;
                                }
                                break;
                            case 1:
                                if (!isMonth(number)) {
                                    success = false;
                                    stopLoop = true;
                                    break;
                                } else {
                                    month = number;
                                }
                                break;
                            case 2:
                                if (isLeap(number)) {
                                    this.maxDaysOfMonth.replace(number, 29);
                                }
                                break;
                            default:
                                break;
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
     * Returns true if the number is between 0 (exclusive) and the maximum amount of days in the given month.
     * @param month The month represented as an integer. 1 = January, 2 = February, etc.
     */
    public boolean isDay(int day, int month) {
        return 0 < day && day <= this.maxDaysOfMonth.get(month);
    }

    /**
     * Returns true if the number is between 0 (exclusive) and 12 (inclusive)
     */
    public static boolean isMonth(int number) {
        return 0 < number && number <= 12;
    }

    /**
     * Checks if the given number is a leap year.
     * A leap year is a year that is divisible by 4. Unless it is divisible by 100. Unless it's divisible by 400.
     */
    public static boolean isLeap(int number) {
        if (number % 400 == 0) {
            return true;
        } else if (number % 100 == 0) {
            return false;
        } else {
            return number % 4 == 0;
        }
    }
}
