package uz.invan.rovitalk.util.ktx

import android.content.ComponentName
import android.content.ServiceConnection

fun interface ServiceConnection : ServiceConnection {
    override fun onServiceDisconnected(componentName: ComponentName?) {

    }
}