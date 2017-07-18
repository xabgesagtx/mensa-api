package com.github.xabgesagtx.mensa.time;

import com.github.xabgesagtx.mensa.config.MensaConfig;
import com.github.xabgesagtx.mensa.model.Mensa;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Utility class to handle time specific question mostly regarding the topic when the mensa will be or was open
 */
@Component
public class TimeUtils {

    @Autowired
    private MensaConfig config;

    private static final HolidayManager HOLIDAY_MANAGER = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.GERMANY));

    /**
     * Returns next opening day or today if the mensa is open on that day
     * @param mensa to check
     * @return next opening day or today
     */
    public LocalDate nextOpeningDay(Mensa mensa) {
        return nextOpeningDay(LocalDate.now(), mensa);
    }

    /**
     * Returns next opening relative to passed date. If the mensa is open on that day, the date is returned
     * @param date to find next opening day for
     * @param mensa to check
     * @return next opening relative to passed date
     */
    public LocalDate nextOpeningDay(LocalDate date, Mensa mensa) {
        LocalDate result = date;
        while (!isOpeningDay(result, mensa)) {
            result = result.plusDays(1);
        }
        return result;
    }

    /**
     * Returns last opening relative to passed date. If the mensa is open on that day, the date is returned
     * @param date to find last opening day for
     * @param mensa to check
     * @return last opening relative to passed date
     */
    public LocalDate lastOpeningDay(LocalDate date, Mensa mensa) {
        LocalDate result = date;
        while (!isOpeningDay(result, mensa)) {
            result = result.minusDays(1);
        }
        return result;
    }

    /**
     * Check if date is opening day for a specific mensa
     * @param date
     * @param mensa
     * @return true if is opening day, false otherwise
     */
    public boolean isOpeningDay(LocalDate date, Mensa mensa) {
        DayOfWeek firstDayClosed = config.isMensaOpenOnSaturday(mensa) ? DayOfWeek.SUNDAY : DayOfWeek.SATURDAY;
        return date.getDayOfWeek().compareTo(firstDayClosed) < 0 && !HOLIDAY_MANAGER.isHoliday(date);
    }

}
