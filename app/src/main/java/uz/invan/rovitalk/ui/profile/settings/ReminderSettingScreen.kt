package uz.invan.rovitalk.ui.profile.settings

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.databinding.ScreenReminderSettingBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.util.ktx.roviClear

@AndroidEntryPoint
class ReminderSettingScreen :
    BaseScreen<ScreenReminderSettingBinding>(true, direction = MainDirections.PROFILE) {
    private val viewModel by viewModels<ReminderSettingViewModel>()
    private val reminders = arrayListOf<ReminderEntity>()
    private val adapter by lazy { RemindersAdapter(reminders) }

    override fun setBinding() = ScreenReminderSettingBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
        viewModel.toggled.observe(viewLifecycleOwner, toggledObserver)
        viewModel.reminders.observe(viewLifecycleOwner, remindersObserver)
        viewModel.reminderEnabled.observe(viewLifecycleOwner, reminderEnabledObserver)
        viewModel.removeReminder.observe(viewLifecycleOwner, removeReminderObserver)
    }

    override fun onViewAttach() {
        binding.back.setOnClickListener { viewModel.exit() }
        binding.dailyRemindersSettingLayout.setOnClickListener {
            viewModel.toggled(!binding.dailyRemindersSwitch.isChecked)
        }
        binding.dailyRemindersSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.reminderEnabled(isChecked)
        }
        with(binding.reminders) {
            adapter = this@ReminderSettingScreen.adapter
            layoutManager = LinearLayoutManager(context)
        }
        with(adapter) {
            setOnReminderClickListener { reminderData ->
                val dialog = AddReminderDialog().apply {
                    reminder = reminderData
                    setOnReminderDismiss {
                        viewModel.retrieveReminders()
                    }
                }
                dialog.show(parentFragmentManager, null)
            }
            setOnReminderRemoveListener { reminder ->
                viewModel.removeReminder(reminder)
            }
        }
        viewModel.retrieveReminders()
        binding.addReminder.setOnClickListener {
            AddReminderDialog().apply {
                setOnReminderDismiss {
                    viewModel.retrieveReminders()
                }
            }.show(parentFragmentManager, null)
        }
    }

    private val exitObserver: Observer<Unit> = Observer {
        exit()
    }
    private val toggledObserver: Observer<Boolean> = Observer { isToggled ->
        binding.dailyRemindersSwitch.toggle()
    }
    private val remindersObserver: Observer<List<ReminderEntity>> = Observer { privateReminders ->
        reminders.roviClear().addAll(privateReminders)
        adapter.notifyDataSetChanged()
    }
    private val reminderEnabledObserver: Observer<Boolean> = Observer { isEnabled ->
        binding.dailyRemindersSwitch.isChecked = isEnabled
    }
    private val removeReminderObserver: Observer<String> = Observer { id ->
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            reminders.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }
}