package uz.invan.rovitalk.data.tools.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import uz.invan.rovitalk.data.models.audio.Player
import uz.invan.rovitalk.util.ktx.createPendingIntent

class PhoneStateBroadcastReceiver:BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)?:return
        when(state){
            TelephonyManager.EXTRA_STATE_RINGING ->{
                val pendingIntent = context?.createPendingIntent(Player.Actions.AUDIO_FOCUS_LOSS)
                pendingIntent?.send()
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK ->{
                val pendingIntent = context?.createPendingIntent(Player.Actions.AUDIO_FOCUS_LOSS)
                pendingIntent?.send()
            }
            TelephonyManager.EXTRA_STATE_IDLE->{
                val pendingIntent = context?.createPendingIntent(Player.Actions.AUDIO_FOCUS_GAIN)
                pendingIntent?.send()
            }
        }
        println(state)
    }
}