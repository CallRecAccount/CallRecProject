package uz.invan.rovitalk.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.local.prefs.RoviPrefsImpl
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.models.feed.Category
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.price.toPricesString
import uz.invan.rovitalk.data.tools.service.PlayerService
import uz.invan.rovitalk.data.tools.workers.ReminderWorker
import uz.invan.rovitalk.databinding.ActivityMainBinding
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.ui.price.PriceDialog
import uz.invan.rovitalk.util.RoviContextWrapper
import uz.invan.rovitalk.util.ktx.*
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<ActivityViewModel>()
    private val playersControllerViewModel: PlayersControllerViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val controller by lazy { findNavController(R.id.navigation_container) }
    var player: PlayerService? = null

    private val playerConnection = ServiceConnection { _, iBinder ->
        val binder = iBinder as PlayerService.PlayerBinder
        player = binder.player()
        playersControllerViewModel.submitPlayer(player)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // prevent screen capture
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel.onNotification.observe(this, onNotificationObserver)
        viewModel.initBottomBar.observe(this, initBottomBarObserver)
        viewModel.changeBottomBarVisibility.observe(this, changeBottomBarVisibilityObserver)
        viewModel.selectBottomBar.observe(this, selectBottomBarObserver)
        viewModel.showPlayerFooter.observe(this, showPlayerFooterObserver)
        viewModel.hidePlayerFooter.observe(this, hidePlayerFooterObserver)

        viewModel.qr(intent?.data)
    }

    private val changeBottomBarVisibilityObserver: Observer<Boolean> = Observer { isVisible ->
        if (!binding.bottomNavigation.isVisible) {
            val height = binding.bottomNavigation.height
            binding.bottomNavigation.translationY = height.toFloat()
        }

        val hideBottomBar = fun() {
            binding.bottomNavigation.isGone = true
        }

        val showBottomBar = fun() {
            binding.bottomNavigation.isVisible = true
            binding.bottomNavigation.animate().translationY(0f).setDuration(300).start()
        }

        if (isVisible) showBottomBar() else hideBottomBar()
    }

    private val onNotificationObserver: Observer<ReminderEntity> = Observer { reminder ->
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
            .setInputData(workDataOf(ReminderWorker.REMINDER_ID to reminder.id))
            .build()

        WorkManager.getInstance(this).cancelAllWorkByTag(reminder.id)
        WorkManager.getInstance(this).enqueueUniqueWork(
            reminder.id,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Timber.d("Added: $reminder")
    }

    private val initBottomBarObserver: Observer<Unit> = Observer {
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> controller.navigate(RoviNavigationDirections.moveHomeMenu())
                R.id.section -> if (controller.currentDestination?.id == R.id.home)
                    controller.navigate(RoviNavigationDirections.moveSectionMenuFromHome())
                else controller.navigate(RoviNavigationDirections.moveSectionMenuFromProfile())
                R.id.profile -> controller.navigate(RoviNavigationDirections.moveProfileMenu())
            }
            return@setOnNavigationItemSelectedListener true
        }
        binding.bottomNavigation.setOnNavigationItemReselectedListener { }
    }

    private val selectBottomBarObserver: Observer<MainDirections> = Observer { direction ->
        binding.bottomNavigation.menu.getItem(direction.ordinal).isChecked = true
    }

    private val showPlayerFooterObserver: Observer<FeedCategory> = Observer { category ->
        val requestOptions =
            RequestOptions().transform(CenterCrop(), RoundedCorners(px(8)))
        Glide.with(this)
            .load(category.image)
            .apply(requestOptions)
            .into(binding.playerFooter.imagePodcast)
        binding.playerFooter.textSectionName.text = category.sectionTitle
        binding.playerFooter.textPodcastTitle.text = category.title

        binding.playerFooter.swipePlayerFooter.isVisible = true
        binding.playerFooter.swipePlayerFooter.animate().translationY(0f).setDuration(300).start()
        binding.playerFooter.containerPlayerFooter.setOnClickListener {
            if (category.count == 0) {
                roviError(getString(R.string.category_empty))
                return@setOnClickListener
            }
            if (category.isActive) {
                when (category.type) {
                    Category.PLAYER.type -> controller.navigate(
                        RoviNavigationDirections.navigatePlayer(category)
                    )
                    Category.LIST.type -> controller.navigate(
                        RoviNavigationDirections.navigateAudios(category)
                    )
                }
            } else PriceDialog().apply {
                setOnPricesSelectedListener {
                    this@MainActivity.controller.navigate(
                        RoviNavigationDirections.paymentSystems(
                            it.toPricesString()
                        )
                    )
                }
            }.show(supportFragmentManager, null)
        }
        binding.playerFooter.close.setOnClickListener {
            playersControllerViewModel.forceReleasePlayer()
            viewModel.disableCurrentAudio()
            viewModel.disableFooter()
        }
    }

    private val hidePlayerFooterObserver: Observer<Unit> = Observer {
        val height =
            binding.playerFooter.swipePlayerFooter.height + binding.bottomNavigation.height
        binding.playerFooter.swipePlayerFooter.animate().translationY(height.toFloat())
            .setDuration(300).start()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, playerConnection, Context.BIND_AUTO_CREATE)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) player?.requestAudioFocus()
//        else player?.requestAudioFocusPreO()
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) return

        val prefs: RoviPrefsImpl = RoviPrefsImpl.apply {
            init(newBase)
        }
        super.attachBaseContext(RoviContextWrapper.wrap(newBase, prefs.language))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        overrideConfiguration?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (controller.currentDestination?.id == MainDirections.HOME.destId)
            if (viewModel.onBackPressed()) super.onBackPressed()
            else Toast.makeText(this, getString(R.string.tap_again_to_exit), Toast.LENGTH_SHORT)
                .show()
        else super.onBackPressed()
    }
}