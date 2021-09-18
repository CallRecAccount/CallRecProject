package uz.invan.rovitalk.ui.profile.language

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.user.LanguageData
import uz.invan.rovitalk.databinding.DialogChangeLanguageBinding
import uz.invan.rovitalk.ui.activity.MainActivity
import uz.invan.rovitalk.ui.base.RoundedBottomSheetDialogFragment
import uz.invan.rovitalk.util.custom.decoration.LanguageItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviClear
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class ChangeLanguageDialog :
    RoundedBottomSheetDialogFragment<DialogChangeLanguageBinding>(R.style.SharkBottomSheetDialogTheme) {
    private val viewModel: ChangeLanguageViewModel by viewModels()
    private val languages = arrayListOf<LanguageData>()
    private val adapter by lazy { LanguagesAdapter(languages) }

    override fun setBinding() = DialogChangeLanguageBinding.inflate(layoutInflater)
    override fun onObserve() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
        viewModel.languages.observe(viewLifecycleOwner, languagesObserver)
        viewModel.save.observe(viewLifecycleOwner, saveObserver)
    }

    override fun onViewReady() {
        viewModel.fetchLanguages()

        binding.back.setOnClickListener { viewModel.exit() }
        binding.textCancel.setOnClickListener { viewModel.exit() }

        with(binding.languages) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ChangeLanguageDialog.adapter
            addItemDecoration(LanguageItemDecoration(px(8f)))
            isNestedScrollingEnabled = false
        }
        adapter.setOnLanguageClickListener { viewModel.language(it.language) }
        binding.save.setOnClickListener { viewModel.saveLanguage() }
    }

    override fun onSheetStateChanged(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
    }

    private val languagesObserver: Observer<List<LanguageData>> = Observer {
        languages.roviClear().addAll(it)
        adapter.notifyDataSetChanged()
    }
    private val exitObserver: EventObserver<Unit> = EventObserver {
        dismiss()
    }
    private val saveObserver: EventObserver<Unit> = EventObserver {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}