package uz.invan.rovitalk.ui.story

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.folioreader.Config
import com.folioreader.FolioReader
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import uz.invan.rovitalk.data.remote.RoviApiHelper
import uz.invan.rovitalk.databinding.PageStoryBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.onBitmap
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.ktx.saveTo
import java.io.File
import javax.inject.Inject

/**
 * Created by Abdulaziz Rasulbek on 28/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
@AndroidEntryPoint
class StoryPage : BaseScreen<PageStoryBinding>(false, null) {
    override fun setBinding(): PageStoryBinding = PageStoryBinding.inflate(layoutInflater)
    private val viewModel by viewModels<StoryViewModel>({ requireParentFragment() })
    private val args by navArgs<StoryPageArgs>()

    private var folioReader: FolioReader? = null

    @Inject
    lateinit var apiHelper: RoviApiHelper

    override fun onViewAttach() {
        Glide.with(binding.blur)
            .asBitmap()
            .load(args.story.image)
            .onBitmap { bitmap ->
                Blurry.with(requireContext()).radius(25).sampling(2)
                    .color(Color.argb(66, 255, 255, 0))
                    .animate()
                    .from(bitmap).into(binding.blur)
                binding.image.setImageBitmap(bitmap)
            }

        binding.viewBook.setOnClickListener {
            val link = "https://files.fm/down.php?i=wpwtmxdk2"
//            val link = "https://www.epubbooks.com/downloads/197"
//            val link = "https://file.re/2021/10/09/liderlarconverio/?download&st=1633792868"
            val path = requireContext().filesDir.path + "/"
            val fileName = "liderlar.epub"
            val filePath = "$path$fileName"
            val file = File(filePath)
//            file.mkdir()
            showProgress()
            /*viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val responseBody = apiHelper.streamFile(link)

                val bookFolder = bookFolder()
                val book = File(bookFolder, fileName)
                if (book.exists()) book.delete()
                val inputStream = responseBody.byteStream()
                val contentLength = responseBody.contentLength()

                try {
                    book.outputStream().use { inputStream.copyTo(it) }
                } catch (e: Exception) {
                    Timber.tag("AAA").e(e)
                } finally {
                    withContext(Dispatchers.Main) {
                        roviSuccess(contentLength.toString())
                        folioReader = FolioReader.get().openBook(book.path)
                            .setConfig(
                                Config().setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL), true
                            )
                    }
                }
            }*/
            link.saveTo(file, viewLifecycleOwner.lifecycleScope) {
                dismissProgress()
                folioReader = FolioReader.get().openBook(file.path)
                    .setConfig(
                        Config().setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL), true
                    )
            }
        }

    }

    fun bookFolder(): File {
        val bookFolder = File(requireContext().filesDir, BOOK_PATH)
        if (!bookFolder.exists()) {
            bookFolder.mkdir()
        }
        return bookFolder
    }

    override fun onDestroy() {
        super.onDestroy()
        FolioReader.clear()
    }

    companion object {
        const val BOOK_PATH = "/books/"
    }
}