package uz.invan.rovitalk.ui.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.ybq.android.spinkit.style.FadingCircle
import uz.invan.rovitalk.databinding.DialogRoviProgressBinding

class RoviProgressDialog : DialogFragment() {
    private var binding: DialogRoviProgressBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DialogRoviProgressBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        binding?.progress?.indeterminateDrawable = FadingCircle()
    }
}