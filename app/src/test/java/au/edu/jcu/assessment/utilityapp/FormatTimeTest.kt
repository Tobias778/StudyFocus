package au.edu.jcu.assessment.utilityapp

import au.edu.jcu.assessment.utilityapp.ui.screens.formatTime
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatTimeTest {
    @Test fun formatsInitialTimer() {
        assertEquals("25:00", formatTime(1500))
    }

    @Test fun formatsSecondsWithLeadingZero() {
        assertEquals("01:09", formatTime(69))
    }

    @Test fun formatsFinishedTimer() {
        assertEquals("00:00", formatTime(0))
    }
}
