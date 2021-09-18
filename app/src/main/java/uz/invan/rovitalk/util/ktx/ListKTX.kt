package uz.invan.rovitalk.util.ktx

// arraylist-ktx
fun <T> ArrayList<T>.roviClear(): ArrayList<T> {
    clear()
    return this
}

fun <T> List<T>.hasOneTime() = size == 1