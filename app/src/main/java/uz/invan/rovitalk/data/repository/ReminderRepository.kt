package uz.invan.rovitalk.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.invan.rovitalk.data.local.database.dao.ReminderDao
import uz.invan.rovitalk.data.local.prefs.RoviPrefs
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.remote.RoviApiHelper
import uz.invan.rovitalk.util.ktx.isTime
import java.util.*
import javax.inject.Inject

interface ReminderRepository {
    /**
     * Sets whether reminding enabled or not
     * @param enabled true if reminding enabled
     * */
    suspend fun switchReminder(enabled: Boolean)

    /**
     * Checks whether reminder is enabled or not
     * @return [Boolean] true if reminder enabled
     * */
    suspend fun isReminderEnabled(): Boolean

    /**
     * Saves new reminder by given params
     * @param title title of reminder which will be show as notification title
     * @param content content of reminder which contains description about it
     * @param time time when reminder will be reminded (in [HH-MM] format)
     * @param reminder reminder object if not null
     * @return [ReminderEntity] data of inserted reminder
     * */
    suspend fun remind(
        title: String,
        content: String,
        time: String,
        reminder: ReminderEntity? = null,
    ): Flow<ReminderEntity>

    /**
     * Retrieves reminder by given [id] from database
     * @param id id of reminder which will be retrieved
     * @return [ReminderEntity] if exists in database
     * */
    suspend fun retrieveReminder(id: String): ReminderEntity?

    /**
     * Retrieves private reminders which inserted by user
     * @return list of [ReminderEntity] if exists one
     * */
    suspend fun retrievePrivateReminders(): List<ReminderEntity>

    /**
     * Remove reminders if exists
     * @param reminder object of [ReminderEntity] which will be removed
     * @return [Int] id of removed [reminder]
     * */
    suspend fun removeReminder(reminder: ReminderEntity): String

    /**
     * Retrieves reminder notifications from server
     * @return stream of [ReminderEntity]
     * */
    suspend fun retrieveReminderNotifications(): Flow<ReminderEntity>
}

class ReminderRepositoryImpl @Inject constructor(
    private val apiHelper: RoviApiHelper,
    private val reminderDao: ReminderDao,
    private val prefs: RoviPrefs,
) : ReminderRepository {
    override suspend fun switchReminder(enabled: Boolean) {
        prefs.configuration = prefs.configuration?.copy(isRemindingEnabled = enabled)
    }

    override suspend fun isReminderEnabled() = prefs.configuration?.isRemindingEnabled ?: false

    override suspend fun remind(
        title: String,
        content: String,
        time: String,
        reminder: ReminderEntity?,
    ): Flow<ReminderEntity> = flow {
        if (title.isEmpty())
            throw ReminderTitleEmptyException(
                data = ReminderExceptionData(
                    exception = ReminderExceptions.TITLE_EMPTY_EXCEPTION
                )
            )

        if (content.isEmpty())
            throw ReminderContentEmptyException(
                data = ReminderExceptionData(
                    exception = ReminderExceptions.CONTENT_EMPTY_EXCEPTION
                )
            )

        if (time.isEmpty() or !time.isTime())
            throw ReminderTimeWrongException(
                data = ReminderExceptionData(
                    exception = ReminderExceptions.TIME_WRONG_EXCEPTION
                )
            )

        // updates if reminder not null, else inserts
        val rm = if (reminder != null) {
            // updating reminder in database
            reminderDao.updateReminder(reminder.copy(title = title, content = content, time = time))
            reminder
        } else {
            // saving reminder to database
            val rm = ReminderEntity(
                title = title,
                content = content,
                time = time,
                isPrivate = true,
                id = UUID.randomUUID().toString()
            )
            reminderDao.insertReminder(rm)
            rm
        }

        emit(rm)
    }

    override suspend fun retrieveReminder(id: String): ReminderEntity? {
        return reminderDao.retrieveReminder(id)
    }

    override suspend fun retrievePrivateReminders(): List<ReminderEntity> {
        return reminderDao.retrieveReminders().filter { it.isPrivate }
    }

    override suspend fun removeReminder(reminder: ReminderEntity): String {
        // remove reminder
        reminderDao.removeReminder(reminder)
        return reminder.id
    }

    override suspend fun retrieveReminderNotifications() = flow {
        // sends retrieve notification request
        val notifications = apiHelper.retrieveNotifications().data
        // clears all not private reminders
        reminderDao.clearPublicReminders()
        notifications.forEach { notification ->
            reminderDao.retrieveReminder(notification.id)
            val reminder = ReminderEntity(
                title = notification.title,
                content = notification.content,
                time = notification.time,
                isPrivate = false,
                id = notification.id
            )
            // saves reminder and gets id
            reminderDao.insertReminder(reminder)

            emit(reminder)
        }
    }
}