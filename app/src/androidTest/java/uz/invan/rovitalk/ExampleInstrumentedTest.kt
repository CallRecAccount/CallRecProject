package uz.invan.rovitalk

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented background_gradient_main_half, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under background_gradient_main_half.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("uz.invan.rovitalk", appContext.packageName)
    }
}