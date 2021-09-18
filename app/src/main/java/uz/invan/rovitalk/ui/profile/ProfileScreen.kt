package uz.invan.rovitalk.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.animation.AnimationUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.feed.Category
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.price.toPricesString
import uz.invan.rovitalk.data.models.user.Profile
import uz.invan.rovitalk.data.models.user.RoviLanguage
import uz.invan.rovitalk.databinding.ScreenProfileBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.ui.feed.FeedAdapter
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.ui.feed.FeedConstructor
import uz.invan.rovitalk.ui.price.PriceDialog
import uz.invan.rovitalk.ui.profile.language.ChangeLanguageDialog
import uz.invan.rovitalk.util.custom.decoration.FeedItemDecoration
import uz.invan.rovitalk.util.ktx.phone
import uz.invan.rovitalk.util.ktx.roviClear
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.lifecycle.EventObserver
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class ProfileScreen : BaseScreen<ScreenProfileBinding>(true, direction = MainDirections.PROFILE) {
    private val viewModel by viewModels<ProfileViewModel>()
    private val config = FeedConfig(arrayListOf())
    private val adapter by lazy { FeedAdapter(config) }

    private var isAnimated = AtomicBoolean(false)

    override fun setBinding() = ScreenProfileBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.onProfile.observe(viewLifecycleOwner, onProfileObserver)
        viewModel.language.observe(viewLifecycleOwner, languageObserver)
        viewModel.onSections.observe(viewLifecycleOwner, onSectionsObserver)
        viewModel.openLink.observe(viewLifecycleOwner, openLinkObserver)
        viewModel.navigateSettings.observe(viewLifecycleOwner, navigateSettingsObserver)
        viewModel.navigateQR.observe(viewLifecycleOwner, navigateQRObserver)
        viewModel.navigateEditProfile.observe(viewLifecycleOwner, navigateEditProfileObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.roleNotFound.observe(viewLifecycleOwner, roleNotFoundObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateSmallLoader.observe(viewLifecycleOwner, updateSmallLoaderObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        binding.support.setOnClickListener {
            val support = getString(R.string.support_telegram_link)
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(support))
                intent.setPackage("org.telegram.messenger")
                startActivity(intent)
            } catch (exception: Exception) {
                viewModel.openLink(support)
            }
        }
        binding.settings.setOnClickListener { viewModel.navigateSettings() }
        binding.qr.setOnClickListener {
            viewModel.navigateQR()
        }
        binding.language.setOnClickListener {
            ChangeLanguageDialog().show(parentFragmentManager, null)
        }
        binding.textEditProfile.setOnClickListener { viewModel.navigateEditProfile() }
        with(binding.feedProfile) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ProfileScreen.adapter
            addItemDecoration(FeedItemDecoration(48f))
        }
        viewModel.loadProfile()
        viewModel.fetchLanguage()
    }

    /*private val initializeFeedObserver: Observer<Unit> = Observer {
        config.items.roviClear().addAll(
            arrayListOf(
                FeedConfig.FeedItems(FeedConfig.FeedTypes.MY_TARIFFS,
                    feedMyTariffsParams = FeedConfig.FeedMyTariffsParams("Sizning aktiv tariflaringiz")),
                FeedConfig.FeedItems(FeedConfig.FeedTypes.BUY,
                    feedBuyParams = FeedConfig.FeedBuyParams(callback = {
//                        PriceDialog.getInstance().show(parentFragmentManager, null)
//                        controller.navigate(RoviNavigationDirections.openPrice())
                    })),
                FeedConfig.FeedItems(FeedConfig.FeedTypes.PODCASTS_VERTICAL,
                    feedPodcastsVerticalParams = FeedConfig.FeedPodcastsVerticalParams("Oxirgi eshitganlaringiz"))
            )
        )
        with(binding.feedProfile) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ProfileScreen.adapter
            addItemDecoration(FeedItemDecoration(48f))
        }
    }*/

    private val navigateSettingsObserver: EventObserver<Unit> = EventObserver {
        controller.navigate(ProfileScreenDirections.navigateSettings())
    }

    private val navigateQRObserver: EventObserver<Unit> = EventObserver {
        val camera = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val isCameraEnabled = camera == PackageManager.PERMISSION_GRANTED
        if (isCameraEnabled)
            controller.navigate(ProfileScreenDirections.navigateQR())
        else requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_CODE)
    }

    private val navigateEditProfileObserver: EventObserver<Unit> = EventObserver {
        controller.navigate(ProfileScreenDirections.navigateEditProfile())
    }

    private val onProfileObserver: EventObserver<Profile> = EventObserver { profile ->
        profile.photo?.run {
            Glide.with(this@ProfileScreen)
                .load(this)
                .circleCrop()
                .into(binding.imagePerson)
        }
        binding.textPersonName.text =
            getString(R.string.first_last_names, profile.firstName, profile.lastName)
        binding.textPhoneNumber.text = profile.phone.phone()
    }

    private val languageObserver: Observer<RoviLanguage> = Observer { language ->
        if (language == null) return@Observer

        val uz = ContextCompat.getDrawable(requireContext(), R.drawable.ic_uzbekistan)
        val ru = ContextCompat.getDrawable(requireContext(), R.drawable.ic_russia)
        val en = ContextCompat.getDrawable(requireContext(), R.drawable.ic_us)

        when (language) {
            RoviLanguage.UZ -> {
                binding.language.setCompoundDrawablesWithIntrinsicBounds(uz, null, null, null)
                binding.language.setText(R.string.language_uz)
            }
            RoviLanguage.RU -> {
                binding.language.setCompoundDrawablesWithIntrinsicBounds(ru, null, null, null)
                binding.language.setText(R.string.language_ru)
            }
            RoviLanguage.EN -> {
                binding.language.setCompoundDrawablesWithIntrinsicBounds(en, null, null, null)
                binding.language.setText(R.string.language_en)
            }
        }
    }

    private val onSectionsObserver: Observer<List<FeedSection>> = Observer { sections ->
        val builderData = FeedConstructor.buildProfile(
            builderProfile = FeedConstructor.FeedProfileBuilder(
                myTariffs = FeedConstructor.FeedMyTariffsBuilder(
                    header = getString(R.string.your_active_tariffs),
                    sections = sections.filter { it.isActive }
                ),
                buy = FeedConstructor.FeedBuyBuilderData(
                    sections = sections,
                    buyCallback = {
                        PriceDialog().apply {
                            setOnPricesSelectedListener {
                                this@ProfileScreen.controller.navigate(RoviNavigationDirections.paymentSystems(
                                    it.toPricesString())
                                )
                            }
                        }.show(parentFragmentManager, null)
                    }
                ),
                lastListened = FeedConstructor.FeedLastListenedBuilder(
                    header = getString(R.string.last_listened),
                    categories = sections.flatMap { it.categories }
                        .filter { it.lastListenedAt != null },
                    callback = { category ->
                        if (category.count == 0) {
                            roviError(getString(R.string.category_empty))
                            return@FeedLastListenedBuilder
                        }
                        if (category.isActive) {
                            when (category.type) {
                                Category.PLAYER.type -> controller.navigate(
                                    ProfileScreenDirections.navigatePlayerFromProfile(category)
                                )
                                Category.LIST.type -> controller.navigate(
                                    ProfileScreenDirections.navigateAudiosFromProfile(category)
                                )
                            }
                        } else PriceDialog().apply {
                            setOnPricesSelectedListener {
                                this@ProfileScreen.controller.navigate(RoviNavigationDirections.paymentSystems(
                                    it.toPricesString())
                                )
                            }
                        }.show(parentFragmentManager, null)
                    }
                )
            )
        )
        config.items.roviClear().addAll(builderData)
        adapter.notifyDataSetChanged()
        if (isAnimated.compareAndSet(false, true)) {
            val animation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
            binding.feedProfile.layoutAnimation = animation
        }
    }

    private val openLinkObserver: EventObserver<String> = EventObserver { url ->
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_could_not_retrieve_data))
    }

    private val unauthorizedObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unauthorized))
        controller.navigate(RoviNavigationDirections.splash())
    }

    private val roleNotFoundObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_role_not_found))
    }

    private val unknownErrorObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unknown_exception))
    }

    private val noInternetObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_no_internet))
    }

    private val timeOutObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_time_out))
    }

    private val updateSmallLoaderObserver: EventObserver<Boolean> =
        EventObserver { isLoaderVisible ->
            if (isLoaderVisible) roviSuccess(getString(R.string.updating))
        }

    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }

    override fun onBackPressed(): Boolean {
        controller.navigate(RoviNavigationDirections.moveHomeMenu())
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_CODE) {
            val camera =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            val isCameraEnabled = camera == PackageManager.PERMISSION_GRANTED
            if (isCameraEnabled)
                controller.navigate(ProfileScreenDirections.navigateQR())
            else roviError(getString(R.string.allow_camera_permission))
        }
    }

    companion object {
        private const val CAMERA_CODE = 42
    }
}