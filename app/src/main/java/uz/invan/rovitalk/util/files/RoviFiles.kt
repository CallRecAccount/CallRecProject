package uz.invan.rovitalk.util.files

import android.content.Context
import java.io.File
import java.io.InputStream

interface RoviFiles {
    /**
     * Application context that used to `CRUD` files
     * */
    val context: Context

    /**
     * Creates audios folder if not exists in [AUDIOS_PATH] path
     * @return [File] folder of audios
     * @throws SecurityException if working with files are disable
     * */
    suspend fun audioFolder(): File

    /**
     * Creates file by [name] and copies [`in`] to file
     * @param name name of audio which will be saved
     * @param `in` input stream which loaded with retrofit streaming
     * @param length length of file
     * @throws SecurityException if working with files are disable
     * */
    suspend fun saveAudio(name: String, `in`: InputStream?, length: Long)

    /**
     * Deletes all audios which name in [audios]
     * @param audios name of audio files which will be deleted
     * @throws SecurityException if working with files are disable
     * */
    suspend fun deleteAudios(audios: List<String>)

    /**
     * Retrieves audios by names which given in [audios]
     * @param audios name of audios which will be retrieved
     * @return map of [String] and [File] objects, where first name of file and
     * second file itself, respectively
     * @throws SecurityException if working with files are disable
     * */
    suspend fun retrieveAudios(audios: List<String>): HashMap<String, File>

    /**
     * Creates if not exists background musics in [BACKGROUND_MUSICS_PATH] path
     * @return [File] folder of background musics
     * @throws SecurityException if working with files are disabled
     * */
    suspend fun bmFolder(): File

    /**
     * Saves given [in] stream to file by [name] and inside [BACKGROUND_MUSICS_PATH] folder
     * @param name name of background music which will be saved as
     * @param in input stream which loaded with retrofit streaming
     * @throws SecurityException if working with files are disabled
     * */
    suspend fun saveBM(name: String, `in`: InputStream?)

    /**
     * Delete all background music files inside [BACKGROUND_MUSICS_PATH] with [bms] item name
     * @param bms list of background musics which will be deleted
     * @throws SecurityException if working with files are disabled
     * */
    suspend fun deleteBMs(bms: List<String>)

    /**
     * Retrieves all background musics by names in [bms]
     * @param bms name of background musics which will be retrieved
     * @return map of `String` to `File`, where name of bm is string and file of this name in folder
     * @throws SecurityException if working with files are disabled
     * */
    suspend fun retrieveBMs(bms: List<String>): Map<String, File>

    /**
     * Creates temporary folder if not exists in [TEMPORARY_PATH] path
     * @return [File] folder of temporary files
     * @throws SecurityException if working with files are disabled
     * */
    suspend fun temporaryFolder(): File

    /**
     * Creates file by [name] and copies [`in`] to file
     * @param name name of temporary file which will be saved
     * @param `in` input stream which loaded with retrofit streaming
     * @param length length of file
     * @throws SecurityException if working with files are disabled
     * */
    suspend fun saveTemporary(name: String, `in`: InputStream?, length: Long)

    /**
     * Copies file with [name] to audios from temporary folder
     * @param name of file which will be copied
     * @throws SecurityException if working with files are disabled
     * */
    suspend fun copyToAudios(name: String)

    /**
     * Copies temporary files to audios and deletes them
     * @throws SecurityException if working with files disabled
     * */
    suspend fun emptyTemporary()

    companion object {
        const val AUDIOS_PATH = "/Audios/"
        const val BACKGROUND_MUSICS_PATH = "/Background musics/"
        const val TEMPORARY_PATH = "/Temporary/"
    }
}