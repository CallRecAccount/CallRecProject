package uz.invan.rovitalk.util

object SearchManager {
    private fun String.toCyrillic(): String {
        val mapper = hashMapOf(
            "a" to "а",
            "b" to "б",
            "d" to "д",
            "e" to "э",
            "f" to "ф",
            "g" to "г",
            "h" to "ҳ",
            "i" to "и",
            "j" to "ж",
            "k" to "к",
            "l" to "л",
            "m" to "м",
            "n" to "н",
            "o" to "о",
            "p" to "п",
            "q" to "қ",
            "r" to "р",
            "s" to "с",
            "t" to "т",
            "u" to "у",
            "v" to "в",
            "x" to "х",
            "y" to "й",
            "z" to "з",
            "o'" to "ў",
            "g'" to "ғ",
            "sh" to "ш",
            "ch" to "ч"
        )

        val builder = StringBuilder()
        for (char in this) {
            if (mapper[char.toString()] != null)
                builder.append(mapper[char.toString()])
            else
                builder.append(char.toString())
        }

        return builder.toString()
    }

    fun String.toLatin(): String {
        val mapper = hashMapOf<String, String>(
            "а" to "a",
            "б" to "b",
            "д" to "d",
            "э" to "e",
            "ф" to "f",
            "г" to "g",
            "ҳ" to "h",
            "и" to "i",
            "ж" to "j",
            "к" to "k",
            "л" to "l",
            "м" to "m",
            "н" to "n",
            "о" to "o",
            "п" to "p",
            "қ" to "q",
            "р" to "r",
            "с" to "s",
            "т" to "t",
            "у" to "u",
            "в" to "v",
            "х" to "x",
            "й" to "y",
            "з" to "z",
            "ў" to "o'",
            "ғ" to "g'",
            "ш" to "sh",
            "ч" to "ch",
        )

        val builder = StringBuilder()
        for (char in this) {
            if (mapper[char.toString()] != null)
                builder.append(mapper[char.toString()])
            else
                builder.append(char.toString())
        }

        return builder.toString()
    }

    fun search(query: String, text: String): Boolean {
        val a = text.toCyrillic()
        val b = query.toCyrillic()
        return a.contains(b, true)
    }
}