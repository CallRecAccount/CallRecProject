package uz.invan.rovitalk.ui.audio

import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.invan.rovitalk.data.models.audio.PlayerAudio

class PlayerAdapter(
    fa: FragmentActivity, private val audios: List<PlayerAudio>,
) : FragmentStateAdapter(fa) {
    override fun getItemCount() = audios.size

    override fun createFragment(position: Int) = PlayerItemScreen().apply {
        val item = audios[position]
        arguments =
            bundleOf(PLAYER_ITEM_POSITION to position, PLAYER_ITEM_CATEGORY to item.category)
    }

    companion object {
        const val PLAYER_ITEM_POSITION = "uz.invan.rovitalk.ui.audio.Companion.PLAYER_ITEM_POSITION"
        const val PLAYER_ITEM_CATEGORY = "uz.invan.rovitalk.ui.audio.Companion.PLAYER_ITEM_CATEGORY"
    }
}