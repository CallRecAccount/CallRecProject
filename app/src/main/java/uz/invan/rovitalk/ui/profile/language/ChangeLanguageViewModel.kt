package uz.invan.rovitalk.ui.profile.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.invan.rovitalk.data.models.user.LanguageData
import uz.invan.rovitalk.data.models.user.RoviLanguage
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class ChangeLanguageViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    private val _languages = MutableLiveData<List<LanguageData>>()
    val languages: LiveData<List<LanguageData>> get() = _languages
    private val _exit = MutableLiveData<Event<Unit>>()
    val exit: LiveData<Event<Unit>> get() = _exit
    private val _save = MutableLiveData<Event<Unit>>()
    val save: LiveData<Event<Unit>> get() = _save

    private var language = auth.fetchLanguage()

    fun fetchLanguages() {
        val roviLang = auth.fetchLanguage()

        _languages.value = RoviLanguage.values().map {
            LanguageData(it, false, it.lang == roviLang.lang)
        }
    }

    fun exit() {
        _exit.value = Event(Unit)
    }

    fun language(lang: RoviLanguage) {
        language = lang
    }

    fun saveLanguage() = viewModel {
        val isChanged = auth.changeLanguage(language.lang)
        if (isChanged)
            _save.postValue(Event(Unit))
    }
}