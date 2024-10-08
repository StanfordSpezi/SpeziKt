package edu.stanford.spezi.modules.storage

import androidx.test.platform.app.InstrumentationRegistry
import edu.stanford.spezi.modules.storage.local.LocalStorage
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting
import org.junit.Test
import java.nio.charset.StandardCharsets
import kotlin.random.Random

class LocalStorageTests {
    data class Letter(val greeting: String)

    @Test
    fun localStorage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val localStorage = LocalStorage(context)

        var greeting = "Hello Paul 👋"
        for (index in 0..Random.nextInt(10)) {
            greeting += "🚀"
        }
        val letter = Letter(greeting = greeting)
        localStorage.store(
            letter,
            type = Letter::class,
            settings = LocalStorageSetting.Unencrypted,
            encode = { letter.greeting.toByteArray(StandardCharsets.UTF_8) }
        )
        val storedLetter: Letter = localStorage.read(
            settings = LocalStorageSetting.Unencrypted,
            type = Letter::class,
            decode = { Letter(it.toString(StandardCharsets.UTF_8)) }
        )

        assert(letter.greeting == storedLetter.greeting)

        localStorage.delete(Letter::class)
        localStorage.delete(storageKey = "Letter")
    }
}
