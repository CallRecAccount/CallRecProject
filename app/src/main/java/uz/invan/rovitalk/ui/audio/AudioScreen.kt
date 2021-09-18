package uz.invan.rovitalk.ui.audio

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import io.feeeei.circleseekbar.CircleSeekBar
import jp.wasabeef.blurry.Blurry
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.audio.*
import uz.invan.rovitalk.databinding.ScreenAudioBinding
import uz.invan.rovitalk.ui.activity.AudioPlayerViewModel
import uz.invan.rovitalk.ui.audio.AudioViewModel.Companion.ID
import uz.invan.rovitalk.ui.audio.AudioViewModel.Companion.URL
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.util.custom.decoration.BackgroundAudiosItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviEnable
import uz.invan.rovitalk.util.ktx.setOnSeekingListener
import uz.invan.rovitalk.util.ktx.toBitmap
import uz.invan.rovitalk.util.lifecycle.EventObserver

@Deprecated("Deprecated due to new player", ReplaceWith("PlayerScreen"))
class AudioScreen :
    BaseScreen<ScreenAudioBinding>(isBottomBarVisible = false, direction = MainDirections.SECTION) {
    private val viewModel by viewModels<AudioViewModel>()
    private val playerVM by activityViewModels<AudioPlayerViewModel>()
    private lateinit var onSeekBarChangeListener: CircleSeekBar.OnSeekBarChangeListener
    private val audio = AudioData(
        ID, URL,
        "Ertakterapiya",
        "O'zlikni anglash",
        BackgroundAudioData(null, 50, null, null)
    )
    private val backgroundAudios = arrayListOf<PlayerBM>()
    private val backgroundAudiosAdapter by lazy { BackgroundAudiosAdapter(backgroundAudios) }
    private var onBackgroundItemChanged: DiscreteScrollView.OnItemChangedListener<BackgroundAudiosAdapter.BackgroundAudioVH>? =
        null

    override fun setBinding() = ScreenAudioBinding.inflate(layoutInflater)
    override fun attachObservers() {
        playerVM.audioReady.observe(requireActivity(), audioReadyObserver)
        playerVM.loading.observe(requireActivity(), loadingObserver)
        playerVM.bufferUpdated.observe(requireActivity(), bufferUpdatedObserver)
        playerVM.audioUpdated.observe(requireActivity(), audioUpdatedObserver)
        playerVM.audioStateUpdated.observe(requireActivity(), audioStateUpdatedObserver)

        viewModel.initBackgroundAudios.observe(viewLifecycleOwner, initBackgroundAudiosObserver)
        viewModel.playAudio.observe(viewLifecycleOwner, playAudioObserver)
        viewModel.pauseAudio.observe(viewLifecycleOwner, pauseAudioObserver)
        viewModel.updateBackgroundAudio.observe(viewLifecycleOwner, updateBackgroundAudioObserver)
        viewModel.stopBackgroundAudio.observe(viewLifecycleOwner, stopBackgroundAudioObserver)
        viewModel.updateVolume.observe(viewLifecycleOwner, updateVolumeObserver)
        viewModel.navigateImage.observe(viewLifecycleOwner, navigateImageObserver)
        viewModel.close.observe(viewLifecycleOwner, onCloseObserver)
    }

    override fun onViewAttach() {
        playerVM.prepare(audio)

        binding.controlButton.setOnClickListener { viewModel.updateControl() }
        onSeekBarChangeListener = CircleSeekBar.OnSeekBarChangeListener { _, curValue ->
            playerVM.seekTo(curValue)
        }
        binding.progressAudio.setOnSeekBarChangeListener(onSeekBarChangeListener)
        binding.voiceController.setOnSeekingListener { viewModel.updateVolume(it) }
        binding.voiceReset.setOnClickListener {
            viewModel.resetVolume()
            binding.voiceController.setProgress(50f)
        }

        binding.close.setOnClickListener { viewModel.close() }
        binding.imageAudio.setOnClickListener { viewModel.navigateImage() }
        val requestOptions =
            RequestOptions().transform(CenterCrop(), RoundedCorners(requireContext().px(12)))
        Glide.with(this)
            .load(R.drawable.image)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_photo_album_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(requestOptions)
            .into(binding.imageAudio)
        Blurry.with(context)
            .radius(10)
            .sampling(8)
            .async()
            .from(toBitmap(R.drawable.image))
            .into(binding.blur)
        toBitmap(R.drawable.image)?.let {
            Palette.from(it).generate { palette ->
                val color = palette?.getDominantColor(ContextCompat.getColor(requireContext(),
                    R.color.colorPrimaryDark))
                updateColors(color!!)
            }
        }
    }

    private val initBackgroundAudiosObserver: Observer<List<BackgroundAudioData>> = Observer {
//        backgroundAudios.roviClear().addAll(it)
        with(binding.scrollBackgroundAudios) {
            adapter = backgroundAudiosAdapter
            setOrientation(DSVOrientation.HORIZONTAL)
            addItemDecoration(BackgroundAudiosItemDecoration(16f))
            setItemTransformer(ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.CENTER)
                .build()
            )
            if (onBackgroundItemChanged != null) removeItemChangedListener(onBackgroundItemChanged!!)
/*            onBackgroundItemChanged =
                DiscreteScrollView.OnItemChangedListener { _, adapterPosition ->
                    backgroundAudios[adapterPosition].volumePercent =
                        audio.backgroundMusic?.volumePercent ?: DEFAULT_VOLUME_PERCENT
                    if (adapterPosition != 0) viewModel.updateBackgroundAudio(backgroundAudios[adapterPosition], adapterPosition)
                    else viewModel.stopBackgroundAudio(adapterPosition)
                    Timber.d("position: $adapterPosition")
                }
            addOnItemChangedListener(onBackgroundItemChanged!!)*/
        }
    }

    private val audioReadyObserver: Observer<Int> = Observer { duration ->
        binding.progressAudio.maxProcess = duration
        binding.progressAudioLoading.maxProcess = duration
        binding.voiceReset.roviEnable = true
        binding.textAmbient.roviEnable = true
        binding.textVocal.roviEnable = true
        binding.voiceController.roviEnable = true
        playerVM.play()
        viewModel.initBackgroundAudios()
//        activityViewModel.savePodcast(audio)
    }
    private val loadingObserver: Observer<Boolean> = Observer { isLoading ->
        binding.progressCircular.isVisible = isLoading
        binding.controlButton.isGone = isLoading
        binding.progressAudio.roviEnable = !isLoading
    }
    private val bufferUpdatedObserver: Observer<Int> = Observer { loadedPosition ->
        binding.progressAudioLoading.curProcess = loadedPosition
    }
    private val audioUpdatedObserver: Observer<Pair<Int, SeekState>> = Observer {
        val currentPosition = it.first
        val seekState = it.second
        if (seekState == SeekState.FORCE) {
            binding.progressAudio.setOnSeekBarChangeListener(null)
            binding.progressAudio.curProcess = currentPosition
            binding.progressAudio.setOnSeekBarChangeListener(onSeekBarChangeListener)
            context?.let {
                Toast.makeText(context, R.string.audio_not_loaded_completely, Toast.LENGTH_SHORT)
                    .show()
            }
        } else
            binding.progressAudio.curProcess = currentPosition
    }
    private val audioStateUpdatedObserver: Observer<AudioState> = Observer { audioState ->
        when (audioState!!) {
            AudioState.PAUSE -> binding.controlButton.setImageResource(R.drawable.ic_play_arrow)
            AudioState.PLAY -> binding.controlButton.setImageResource(R.drawable.ic_pause_arrow)
        }
        viewModel.setAudioState(audioState)
    }

    private val playAudioObserver: Observer<Unit> = Observer { playerVM.play() }
    private val pauseAudioObserver: Observer<Unit> = Observer { playerVM.pause() }
    private val updateBackgroundAudioObserver: Observer<BackgroundAudioData> = Observer {
        audio.backgroundMusic = it
        audio.updateReason = AudioUpdateReasons.BACKGROUND_AUDIO_UPDATE
        playerVM.updateAudio(audio)
    }
    private val stopBackgroundAudioObserver: Observer<Unit> = Observer {
        audio.backgroundMusic = null
        audio.updateReason = AudioUpdateReasons.BACKGROUND_AUDIO_STOP
        playerVM.updateAudio(audio)
    }
    private val updateVolumeObserver: Observer<Int> = Observer { percent ->
        audio.backgroundMusic?.volumePercent = percent
        audio.updateReason = AudioUpdateReasons.VOLUME_UPDATE
        playerVM.updateAudio(audio)
    }

    private val navigateImageObserver: EventObserver<Unit> = EventObserver {
        val extras = FragmentNavigatorExtras(
            binding.imageAudio to getString(R.string.image)
        )
        controller.navigate(AudioScreenDirections.navigateImage(), extras)
    }

    private val onCloseObserver: Observer<Unit> = Observer {
        exit()
    }
}