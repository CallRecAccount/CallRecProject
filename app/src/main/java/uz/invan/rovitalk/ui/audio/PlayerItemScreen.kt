package uz.invan.rovitalk.ui.audio

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import io.feeeei.circleseekbar.CircleSeekBar
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.databinding.ScreenPlayerItemBinding
import uz.invan.rovitalk.ui.activity.PlayersControllerViewModel
import uz.invan.rovitalk.ui.audio.PlayerAdapter.Companion.PLAYER_ITEM_CATEGORY
import uz.invan.rovitalk.ui.audio.PlayerAdapter.Companion.PLAYER_ITEM_POSITION
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviEnable
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class PlayerItemScreen : BaseScreen<ScreenPlayerItemBinding>(false, null) {
    private val viewModel by viewModels<PlayerItemViewModel>()
    private val playersControllerViewModel by activityViewModels<PlayersControllerViewModel>()
    private var listener: CircleSeekBar.OnSeekBarChangeListener? = null

    override fun setBinding() = ScreenPlayerItemBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.initializePlayerItems.observe(viewLifecycleOwner, initializePlayerItemsObserver)
        viewModel.audioImage.observe(viewLifecycleOwner, audioImageObserver)
        viewModel.audioTitle.observe(viewLifecycleOwner, audioTitleObserver)
        viewModel.audioAuthor.observe(viewLifecycleOwner, audioAuthorObserver)
        viewModel.audioLoading.observe(viewLifecycleOwner, audioLoadingObserver)
        viewModel.audioReady.observe(viewLifecycleOwner, audioReadyObserver)
        viewModel.audioPlay.observe(viewLifecycleOwner, audioPlayObserver)
        viewModel.audioPause.observe(viewLifecycleOwner, audioPauseObserver)
        viewModel.audioSeek.observe(viewLifecycleOwner, audioSeekObserver)
        viewModel.audioLoaderSeek.observe(viewLifecycleOwner, audioLoaderSeekObserver)
        viewModel.audioReleased.observe(viewLifecycleOwner, audioReleasedObserver)
    }

    override fun onViewAttach() {
        viewModel.initializePlayerItems()

        val position = arguments?.getInt(PLAYER_ITEM_POSITION) ?: return
        val category = arguments?.getString(PLAYER_ITEM_CATEGORY) ?: return
        playersControllerViewModel.submitPlayerItemListener(position, category, viewModel)
        binding.controlButton.setOnClickListener {
            playersControllerViewModel.control(position, category)
        }
        listener = CircleSeekBar.OnSeekBarChangeListener { _, curValue ->
            playersControllerViewModel.seekTo(position, curValue)
        }
    }

    private val initializePlayerItemsObserver: Observer<Unit> = Observer {
        binding.controlButton.isVisible = true
        binding.progressCircular.isGone = true
        binding.progressAudio.roviEnable = false
        binding.progressAudio.curProcess = 0
        binding.progressAudio.setOnSeekBarChangeListener(listener)
    }

    private val audioImageObserver: Observer<String> = Observer { image ->
        val requestOptions =
            RequestOptions().transform(CenterCrop(), RoundedCorners(requireContext().px(8)))
        Glide.with(this)
            .load(image)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_photo_album_24)
            .apply(requestOptions)
            .into(binding.imageAudio)
    }
    private val audioTitleObserver: Observer<String> = Observer { title ->
        binding.textAudioName.text = title
    }
    private val audioAuthorObserver: Observer<String> = Observer { author ->
        binding.textAuthor.text = author
    }
    private val audioLoadingObserver: Observer<Unit> = Observer {
        binding.progressCircular.isVisible = true
        binding.controlButton.isGone = true
        binding.progressAudio.roviEnable = false
        binding.textTime.isGone = true
    }
    private val audioReadyObserver: Observer<Int> = Observer { duration ->
        binding.progressCircular.isGone = true
        binding.controlButton.isVisible = true
        binding.progressAudio.roviEnable = true
        binding.progressAudio.maxProcess = duration
        binding.progressAudioLoading.maxProcess = Credentials.MAX_BUFFER_POSITION
        binding.textTime.isVisible = true

        // settings time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        binding.textTime.text = getString(R.string.x_minute_seconds, minutes, seconds)
    }
    private val audioPlayObserver: Observer<Unit> = Observer {
        binding.controlButton.setImageResource(R.drawable.ic_pause_player)
    }
    private val audioPauseObserver: Observer<Unit> = Observer {
        binding.controlButton.setImageResource(R.drawable.ic_play_player)
    }
    private val audioSeekObserver: Observer<Int> = Observer { position ->
        binding.progressAudio.setOnSeekBarChangeListener(null)
        binding.progressAudio.curProcess = position
        binding.progressAudio.setOnSeekBarChangeListener(listener)

        // set time
        val max = binding.progressAudio.maxProcess
        val duration = position - max
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        binding.textTime.text = getString(R.string.x_minute_seconds, abs(minutes), abs(seconds))
    }
    private val audioLoaderSeekObserver: Observer<Int> = Observer { position ->
        binding.progressAudioLoading.curProcess = position
    }
    private val audioReleasedObserver: Observer<Unit> = Observer {
        binding.progressAudio.curProcess = 0
        binding.progressAudioLoading.curProcess = 0
        binding.controlButton.setImageResource(R.drawable.ic_play_arrow)
        binding.controlButton.isVisible = true
        binding.progressCircular.isGone = true
        binding.progressAudio.roviEnable = false
        binding.textTime.isGone = true
    }
}