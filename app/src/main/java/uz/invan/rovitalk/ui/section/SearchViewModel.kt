package uz.invan.rovitalk.ui.section

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.roviClear
import uz.invan.rovitalk.util.ktx.viewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _onQuery = MutableLiveData<Pair<String, List<FeedSection>>>()
    val onQuery: LiveData<Pair<String, List<FeedSection>>> get() = _onQuery
    private val _empty = MutableLiveData<Unit>()
    val empty: LiveData<Unit> get() = _empty
    private val _notFound = MutableLiveData<Unit>()
    val notFound: LiveData<Unit> get() = _notFound

    private val sections = arrayListOf<FeedSection>()

    init {
        retrieveSections()
    }

    private fun retrieveSections() = viewModel {
        podcasts.retrieveSectionsAndCategories().catch { exception ->
            Timber.d(exception)
        }.collect { resource ->
            if (resource is Resource.Success && resource.data != null && sections.isEmpty())
                sections.roviClear().addAll(resource.data)
        }
    }

    fun onQuery(query: String) {
        _onQuery.postValue(query to sections)
        Timber.tag("MY_TAG_VM").d("Sections: $sections")
    }

    fun empty() {
        _empty.value = Unit
    }

    fun notFound() {
        _notFound.value = Unit
    }
}