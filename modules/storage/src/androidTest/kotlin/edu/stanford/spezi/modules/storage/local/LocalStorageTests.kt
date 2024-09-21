package edu.stanford.spezi.modules.storage.local

import org.junit.Test
import kotlin.random.Random

class LocalStorageTests {
    data class Letter(val greeting: String)

    @Test
    fun localStorage() {
        val localStorage = LocalStorage()

        var greeting = "Hello Paul 👋"
        for (index in 0..Random.nextInt(10)) {
            greeting += "🚀"
        }
        val letter = Letter(greeting = greeting)
        localStorage.store(letter, settings = LocalStorageSetting.Unencrypted)
        val storedLetter: Letter = localStorage.read(settings = LocalStorageSetting.Unencrypted)

        assert(letter.greeting == storedLetter.greeting)

        localStorage.delete(Letter::class)
        localStorage.delete(storageKey = "Letter")
    }
}