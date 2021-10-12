package uz.invan.rovitalk.ui

import uz.invan.rovitalk.databinding.ScreenBookViewBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import java.io.File

/**
 * Created by Abdulaziz Rasulbek on 29/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class BookViewScreen : BaseScreen<ScreenBookViewBinding>(false, null) {
    override fun setBinding(): ScreenBookViewBinding = ScreenBookViewBinding.inflate(layoutInflater)

    override fun onViewAttach() {
        /*binding.pdfView.fromFile(File("${requireContext().cacheDir.path}/downloaded_pdf.pdf"))
            .enableDoubletap(true)
            .enableSwipe(true)
            .swipeHorizontal(true)
            .enableAnnotationRendering(true)
            .enableAntialiasing(true)
            .pageSnap(true)
            .nightMode(true)
            .load()*/
    }

}