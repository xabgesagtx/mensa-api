package com.github.xabgesagtx.mensa.time;

import com.github.xabgesagtx.mensa.config.MensaConfig;
import com.github.xabgesagtx.mensa.model.Mensa;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TimeUtilsTest {

    private static final String MENSA_ID_CLOSED_ON_SATURDAY = "100";
    private static final String MENSA_ID_OPEN_ON_SATURDAY = "101";

    @Spy
    private MensaConfig mensaConfig;

    @InjectMocks
    private TimeUtils timeUtils;

    private Mensa mensaOpenOnSaturday = Mensa.builder().id(MENSA_ID_OPEN_ON_SATURDAY).build();
    private Mensa mensaClosedOnSaturday = Mensa.builder().id(MENSA_ID_CLOSED_ON_SATURDAY).build();

    @Before
    public void setUp() {
        mensaConfig.setIdsOpenOnSaturday(Arrays.asList(MENSA_ID_OPEN_ON_SATURDAY));
    }


    @Test
    public void nextOpeningDayClosedOnSaturday() throws Exception {
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 26), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 26)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 25), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 26)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 24), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 26)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 23), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 23)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 5, 1), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 5, 2)));
    }

    @Test
    public void nextOpeningDayOpenOnSaturday() throws Exception {
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 26), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 26)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 25), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 26)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 24), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 24)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 6, 23), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 23)));
        assertThat(timeUtils.nextOpeningDay(LocalDate.of(2017, 5, 1), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 5, 2)));
    }

    @Test
    public void lastOpeningDayClosedOnSaturday() throws Exception {
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 26), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 26)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 25), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 23)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 24), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 23)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 23), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 6, 23)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 5, 1), mensaClosedOnSaturday), equalTo(LocalDate.of(2017, 4, 28)));
    }

    @Test
    public void lastOpeningDayOpenOnSaturday() throws Exception {
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 26), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 26)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 25), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 24)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 24), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 24)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 6, 23), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 6, 23)));
        assertThat(timeUtils.lastOpeningDay(LocalDate.of(2017, 5, 1), mensaOpenOnSaturday), equalTo(LocalDate.of(2017, 4, 29)));
    }

    @Test
    public void isOpeningDayClosedOnSaturday() throws Exception {
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 26), mensaClosedOnSaturday), equalTo(true));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 25), mensaClosedOnSaturday), equalTo(false));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 24), mensaClosedOnSaturday), equalTo(false));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 23), mensaClosedOnSaturday), equalTo(true));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 5, 1), mensaClosedOnSaturday), equalTo(false));
    }

    @Test
    public void isOpeningDayOpenOnSaturday() throws Exception {
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 26), mensaOpenOnSaturday), equalTo(true));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 25), mensaOpenOnSaturday), equalTo(false));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 24), mensaOpenOnSaturday), equalTo(true));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 6, 23), mensaOpenOnSaturday), equalTo(true));
        assertThat(timeUtils.isOpeningDay(LocalDate.of(2017, 5, 1), mensaOpenOnSaturday), equalTo(false));
    }
    
}