package uz.invan.rovitalk.ui.profile.settings

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.akexorcist.snaptimepicker.TimeValue
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.tools.workers.ReminderWorker
import uz.invan.rovitalk.data.tools.workers.ReminderWorker.Companion.REMINDER_ID
import uz.invan.rovitalk.databinding.DialogAddReminderBinding
import uz.invan.rovitalk.ui.base.RoundedBottomSheetDialogFragment
import uz.invan.rovitalk.util.ktx.hour
import uz.invan.rovitalk.util.ktx.minutes
import uz.invan.rovitalk.util.ktx.pad
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AddReminderDialog :
    RoundedBottomSheetDialogFragment<DialogAddReminderBinding>(R.style.SharkBottomSheetDialogTheme) {
    private val viewModel by viewModels<AddReminderViewModel>()
    var reminder: ReminderEntity? = null
    private var reminderDismissListener: OnReminderDismissListener? = null

    fun setOnReminderDismiss(onReminderDismiss: OnReminderDismissListener) {
        reminderDismissListener = onReminderDismiss
    }

    override fun setBinding() = DialogAddReminderBinding.inflate(layoutInflater)
    override fun onObserve() {
        viewModel.reminderSaved.observe(viewLifecycleOwner, reminderSavedObserver)
    }

    override fun onViewReady() {
        isCancelable = false
        binding.back.setOnClickListener { dismiss() }
        binding.textCancel.setOnClickListener { dismiss() }
        // initialize data if not null
        reminder?.let { reminder ->
            binding.editableReminderTitle.setText(reminder.title)
            binding.editableReminderText.setText(reminder.content)
            binding.remindTime.setText(reminder.time)
        }
        binding.remindTime.setOnClickListener {
            SnapTimePickerDialog.Builder().apply {
                reminder?.let { reminder ->
                    setPreselectedTime(TimeValue(reminder.time.hour(), reminder.time.minutes()))
                } ?: run {
                    val calendar = Calendar.getInstance()
                    setPreselectedTime(
                        TimeValue(calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE])
                    )
                }
                setTitle(R.string.choose_remind_time)
                setThemeColor(R.color.colorBackground)
                setTitleColor(R.color.colorPrimary)
            }.build().apply {
                setListener { hour, minute ->
                    binding.remindTime.setText(getString(R.string.x_time, hour.pad(), minute.pad()))
                }
            }.show(parentFragmentManager, null)
        }
        binding.remind.setOnClickListener {
            viewModel.remind(
                title = binding.editableReminderTitle.text.toString(),
                content = binding.editableReminderText.text.toString(),
                time = binding.remindTime.text.toString(),
                rm = reminder
            )
        }
    }

    override fun onSheetStateChanged(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
    }

    private val reminderSavedObserver: Observer<ReminderEntity> = Observer { reminder ->
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, reminder.time.hour())
        calendar.set(Calendar.MINUTE, reminder.time.minutes())
        calendar.set(Calendar.SECOND, 0)

        if (calendar.before(now))
            calendar.add(Calendar.HOUR_OF_DAY, 24)

        val timeDiff = calendar.timeInMillis - now.timeInMillis
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag(reminder.id)
            .setInputData(workDataOf(REMINDER_ID to reminder.id))
            .build()

        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(reminder.id)
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            reminder.id,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Timber.d("On reminder dismiss, listener $reminderDismissListener")
        reminderDismissListener?.onReminderDismiss()
        dismiss()
    }

    fun interface OnReminderDismissListener {
        fun onReminderDismiss()
    }
}