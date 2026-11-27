package com.kallpa.base.ui

import android.content.pm.ActivityInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.kallpa.base.MainActivity
import com.kallpa.base.data.RoomClientRepository
import com.kallpa.base.data.local.KallpaDatabase
import com.kallpa.base.domain.Client
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class ClientSearchRotationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun querySobreviveRotacion() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Buscar cliente").performTextInput("ana")
        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Luis Gómez").assertDoesNotExist()

        composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Ana Pérez").fetchSemanticsNodes().isNotEmpty()
        }

        val queryValue = composeTestRule.onNodeWithContentDescription("Buscar cliente")
            .fetchSemanticsNode().config[SemanticsProperties.EditableText].text
        assertEquals("ana", queryValue)
        composeTestRule.onNodeWithText("Ana Pérez").assertExists()
        composeTestRule.onNodeWithText("Luis Gómez").assertDoesNotExist()
    }

    companion object {
        private val context = InstrumentationRegistry.getInstrumentation().targetContext

        @JvmStatic
        @BeforeClass
        fun seedDatabase() = runBlocking {
            context.deleteDatabase(KallpaDatabase.DATABASE_NAME)
            val database = Room.databaseBuilder(
                context,
                KallpaDatabase::class.java,
                KallpaDatabase.DATABASE_NAME,
            ).build()
            val repository = RoomClientRepository(database.clientDao())
            repository.save(Client(id = "1", nombre = "Ana Pérez"))
            repository.save(Client(id = "2", nombre = "Luis Gómez"))
            database.close()
        }

        @JvmStatic
        @AfterClass
        fun cleanDatabase() {
            context.deleteDatabase(KallpaDatabase.DATABASE_NAME)
        }
    }
}
