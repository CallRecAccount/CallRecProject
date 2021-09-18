package uz.invan.rovitalk.ui.profile.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.BuildConfig
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.settings.ResourceLinks
import uz.invan.rovitalk.data.models.settings.RoviSettings
import uz.invan.rovitalk.data.models.settings.SettingsLinks
import uz.invan.rovitalk.databinding.ScreenSettingsBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.util.custom.decoration.SettingsItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviClear
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class SettingsScreen : BaseScreen<ScreenSettingsBinding>(true, direction = MainDirections.PROFILE) {
    private val viewModel by viewModels<SettingsViewModel>()
    private val data = arrayListOf<RoviSettings>()
    private val adapter by lazy { SettingsAdapter(data) }

    override fun setBinding() = ScreenSettingsBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
        viewModel.initializeSettings.observe(viewLifecycleOwner, initializeSettingsObserver)
        viewModel.openLink.observe(viewLifecycleOwner, openLinkObserver)
        viewModel.navigateSetting.observe(viewLifecycleOwner, navigateSettingObserver)
        viewModel.logoutWarning.observe(viewLifecycleOwner, logoutWarningObserver)
        viewModel.logout.observe(viewLifecycleOwner, logoutObserver)
    }

    override fun onViewAttach() {
        binding.back.setOnClickListener { viewModel.exit() }
        viewModel.initializeSettings()
        binding.logout.setOnClickListener { viewModel.logoutWarning() }
        binding.version.text = getString(R.string.app_version_x, BuildConfig.VERSION_NAME)
    }

    private val exitObserver: Observer<Unit> = Observer {
        exit()
    }

    private val initializeSettingsObserver: Observer<Unit> = Observer {
        data.roviClear().addAll(RoviSettings.values())
        with(binding.settings) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SettingsScreen.adapter.apply {
                setOnNavigateSettings { viewModel.navigateSetting(it) }
            }
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            addItemDecoration(SettingsItemDecoration(context.px(1f)))
        }
    }

    private val openLinkObserver: EventObserver<String> = EventObserver { url ->
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    private val navigateSettingObserver: EventObserver<Pair<RoviSettings, SettingsLinks?>> =
        EventObserver { pair ->
            val setting = pair.first
            val settingsLinks = pair.second
            when (setting) {
                RoviSettings.FAQ -> {
                    val faq =
                        settingsLinks?.links?.firstOrNull { it.type == ResourceLinks.QUESTION_AND_ANSWERS.title }
                    if (faq != null)
                        viewModel.openLink(faq.url)
                }
                RoviSettings.DAILY_NOTIFICATIONS -> {
                    controller.navigate(SettingsScreenDirections.moveReminderSettings())
                }
                RoviSettings.RESTORE_PURCHASE -> {
                    controller.navigate(SettingsScreenDirections.moveRestorePurchase())
                }
                RoviSettings.ABOUT_ROVI -> {
                    val about =
                        settingsLinks?.links?.firstOrNull { it.type == ResourceLinks.ABOUT_APP.title }
                    if (about != null)
                        viewModel.openLink(about.url)
                }
                RoviSettings.APP_RATING -> {
                    val rating =
                        "http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                    viewModel.openLink(rating)
                }
                RoviSettings.COMMENT -> {
                    val subject = getString(R.string.feedback_subject)
                    val text = getString(R.string.feedback_text,
                        BuildConfig.VERSION_NAME,
                        Build.VERSION.SDK_INT
                    )

                    val email = Intent(Intent.ACTION_SENDTO)
                    email.data = Uri.parse("mailto:")
                    email.putExtra(Intent.EXTRA_EMAIL, arrayOf("approvitalk@gmail.com"))
                    email.putExtra(Intent.EXTRA_SUBJECT, subject)
                    email.putExtra(Intent.EXTRA_TEXT, text)
                    try {
                        startActivity(email)
                    } catch (exception: Exception) {
                        roviError(getString(R.string.feedback_not_available))
                    }
                }
                RoviSettings.LAW_INSTRUCTIONS -> {
                    val law =
                        settingsLinks?.links?.firstOrNull { it.type == ResourceLinks.LEGAL_INFORMATION.title }
                    if (law != null)
                        viewModel.openLink(law.url)
                }
            }
        }

    private val logoutWarningObserver: EventObserver<Unit> = EventObserver {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.are_you_sure))
            .setContentText(getString(R.string.your_all_data_will_be_deleted))
            .setCancelText(getString(R.string.no_want_to_stay))
            .setConfirmText(getString(R.string.yes_want_to_log_out))
            .setCancelButtonBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
            .setConfirmButtonBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.fern)
            )
            .setConfirmClickListener {
                it.dismissWithAnimation()
                viewModel.logout()
            }
            .show()
    }

    private val logoutObserver: EventObserver<Unit> = EventObserver {
        controller.navigate(RoviNavigationDirections.splash())
    }
}