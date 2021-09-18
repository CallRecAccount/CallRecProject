package uz.invan.rovitalk.ui.video

import android.net.Uri
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import uz.invan.rovitalk.R
import uz.invan.rovitalk.databinding.ScreenVideoBinding
import uz.invan.rovitalk.ui.base.BaseScreen

class VideoScreen : BaseScreen<ScreenVideoBinding>(false, null) {
    private val args by navArgs<VideoScreenArgs>()

    override fun setBinding() = ScreenVideoBinding.inflate(layoutInflater)
    override fun onViewAttach() {
        // shows progress
        showProgress()

        val mediaController = MediaController(context)
        mediaController.setAnchorView(binding.videoPlayer)

        val uri = Uri.parse(args.introduction)
        binding.videoPlayer.setMediaController(mediaController)
        binding.videoPlayer.setVideoURI(uri)
        binding.videoPlayer.start()
        binding.videoPlayer.setOnPreparedListener { dismissProgress() }

        binding.close.setOnClickListener { exit() }
        binding.videoContainer.setOnClickListener { if (!mediaController.isShowing) mediaController.show() }
    }

    override fun statusColor() = ContextCompat.getColor(requireContext(), R.color.black)
}