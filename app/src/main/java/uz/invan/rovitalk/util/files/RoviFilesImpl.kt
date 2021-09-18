@file:Suppress("BlockingMethodInNonBlockingContext")

package uz.invan.rovitalk.util.files

import android.content.Context
import uz.invan.rovitalk.util.files.RoviFiles.Companion.AUDIOS_PATH
import uz.invan.rovitalk.util.files.RoviFiles.Companion.BACKGROUND_MUSICS_PATH
import uz.invan.rovitalk.util.files.RoviFiles.Companion.TEMPORARY_PATH
import java.io.File
import java.io.InputStream

class RoviFilesImpl private constructor(override val context: Context) : RoviFiles {
    companion object {
        @Volatile
        private var INSTANCE: RoviFilesImpl? = null

        fun initialize(context: Context): RoviFilesImpl {
            val temporaryInstance = INSTANCE
            temporaryInstance?.let { return temporaryInstance }

            synchronized(this) {
                val instance = RoviFilesImpl(context)
                INSTANCE = instance
                return instance
            }
        }
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun audioFolder(): File {
        val audioFolder = File(context.filesDir, AUDIOS_PATH)
        if (!audioFolder.exists())
            audioFolder.mkdir()

        return audioFolder
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun saveAudio(name: String, `in`: InputStream?, length: Long) {
        if (`in` == null) return

        val audioFolder = audioFolder()

        val audio = File(audioFolder, name)
        if (audio.exists()) {
            deleteAudios(listOf(name))
        }

        try {
            audio.outputStream().use { fileOut -> `in`.copyTo(fileOut) }
        } catch (exception: Exception) {
            audio.delete()
            exception.printStackTrace()
        }
    }

    override suspend fun deleteAudios(audios: List<String>) {
        val audioFolder = audioFolder()

        val audioFiles = audioFolder.list()
        audioFiles?.forEach { audio ->
            if (audio in audios) {
                val audioFile = File(audioFolder, audio)
                audioFile.delete()
            }
        }
    }

    override suspend fun retrieveAudios(audios: List<String>): HashMap<String, File> {
        val audiosMap = hashMapOf<String, File>()

        val audioFolder = audioFolder()

        val audioFiles = audioFolder.list()
        audioFiles?.forEach { audio ->
            if (audio in audios) {
                val audioFile = File(audioFolder, audio)
                audiosMap[audio] = audioFile
            }
        }

        return audiosMap
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun bmFolder(): File {
        val bmFolder = File(context.filesDir, BACKGROUND_MUSICS_PATH)
        if (!bmFolder.exists())
            bmFolder.mkdir()

        return bmFolder
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun saveBM(name: String, `in`: InputStream?) {
        if (`in` == null) return

        val bmFolder = bmFolder()

        val bm = File(bmFolder, name)
        try {
            bm.outputStream().use { fileOut -> `in`.copyTo(fileOut) }
        } catch (exception: Exception) {
            bm.delete()
            exception.printStackTrace()
        }
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun deleteBMs(bms: List<String>) {
        val bmFolder = bmFolder()

        val bmFiles = bmFolder.list()
        bmFiles?.forEach { bm ->
            if (bm in bms) {
                val bmFile = File(bmFolder, bm)
                bmFile.delete()
            }
        }
    }

    override suspend fun retrieveBMs(bms: List<String>): Map<String, File> {
        val bmsMap = hashMapOf<String, File>()

        val bmFolder = bmFolder()

        val bmFiles = bmFolder.list()
        bmFiles?.forEach { bm ->
            if (bm in bms) {
                val bmFile = File(bmFolder, bm)
                bmsMap[bm] = bmFile
            }
        }
        return bmsMap
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun temporaryFolder(): File {
        val temporaryFolder = File(context.filesDir, TEMPORARY_PATH)
        if (!temporaryFolder.exists())
            temporaryFolder.mkdir()

        return temporaryFolder
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun saveTemporary(name: String, `in`: InputStream?, length: Long) {
        if (`in` == null) return

        val temporaryFolder = temporaryFolder()

        val temporary = File(temporaryFolder, name)
        try {
            temporary.outputStream().use { fileOut -> `in`.copyTo(fileOut) }
        } catch (exception: Exception) {
            temporary.delete()
            exception.printStackTrace()
        }
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun copyToAudios(name: String) {
        val temporaryFolder = temporaryFolder()
        val temporary = File(temporaryFolder, name)

        if (!temporary.exists()) return

        val audioFolder = audioFolder()
        val audio = File(audioFolder, name)

        if (audio.exists())
            audio.delete()

        temporary.copyTo(
            target = audio,
            overwrite = true
        )
        temporary.delete()
    }

    @Throws(exceptionClasses = [SecurityException::class])
    override suspend fun emptyTemporary() {
        val temporaryFolder = temporaryFolder()
        val temporaryFiles = temporaryFolder.list()
        temporaryFiles?.forEach { temporary ->
            copyToAudios(temporary)
        }
    }
}