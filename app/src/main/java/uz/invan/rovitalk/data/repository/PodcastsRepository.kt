package uz.invan.rovitalk.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import uz.invan.rovitalk.data.local.database.dao.AudioDao
import uz.invan.rovitalk.data.local.database.dao.BMDao
import uz.invan.rovitalk.data.local.database.dao.PriceDao
import uz.invan.rovitalk.data.local.database.dao.SectionsAndCategoriesDao
import uz.invan.rovitalk.data.local.prefs.RoviPrefs
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.audio.BMState
import uz.invan.rovitalk.data.models.audio.PlayerAudio
import uz.invan.rovitalk.data.models.audio.PlayerBM
import uz.invan.rovitalk.data.models.entities.*
import uz.invan.rovitalk.data.models.feed.*
import uz.invan.rovitalk.data.models.network.requests.BuyRequest
import uz.invan.rovitalk.data.models.network.requests.QRRequest
import uz.invan.rovitalk.data.models.network.requests.SubCategoriesRequest
import uz.invan.rovitalk.data.models.network.responses.*
import uz.invan.rovitalk.data.models.price.Tariff
import uz.invan.rovitalk.data.models.price.TariffData
import uz.invan.rovitalk.data.models.settings.ResourceLinks
import uz.invan.rovitalk.data.models.settings.SettingsLinkData
import uz.invan.rovitalk.data.models.settings.SettingsLinks
import uz.invan.rovitalk.data.models.validation.RoviConfiguration
import uz.invan.rovitalk.data.models.validation.exceptions.SectionByIdNotFoundException
import uz.invan.rovitalk.data.models.validation.exceptions.SectionsExceptionData
import uz.invan.rovitalk.data.models.validation.exceptions.SectionsExceptions
import uz.invan.rovitalk.data.remote.RoviApiHelper
import uz.invan.rovitalk.util.files.RoviFiles
import uz.invan.rovitalk.util.ktx.separateFromBase
import java.util.*
import javax.inject.Inject

interface PodcastsRepository {
    /**
     * Sections and categories update minimum interval
     * */
    val minSectionsUpdateIntervalInMinutes: Int

    /**
     * Retrieves all sections
     * Must call in [CoroutineScope]
     * */
    suspend fun retrieveSections(): Flow<Resource<List<SectionResponseData>>>

    /**
     * Retrieves all categories by given [sectionId]
     * Must call in [CoroutineScope]
     * @param sectionId id of section which should be categories retrieved
     * */
    suspend fun retrieveCategories(sectionId: String): Flow<Resource<FeedSection>>

    /**
     * Retrieves sections and categories in all sections
     * Must call in [CoroutineScope]
     * @param ignoreCache if true ignores emit from database
     * @return List of feed sections
     * */
    suspend fun retrieveSectionsAndCategories(ignoreCache: Boolean = false): Flow<Resource<List<FeedSection>>>

    /**
     * Inserts feed sections and categories to database
     * Must call in [CoroutineScope]
     * @param feedSections List of feed sections and feed categories inside
     * */
    suspend fun insertFeedSectionsAndCategoriesToDatabase(feedSections: List<FeedSection>)

    /**
     * Inserts feed categories to database
     * Must call in [CoroutineScope]
     * @param `sectionId` Id of section which feed categories belong to
     * @param `feedCategories` List of feed categories which will be inserted
     * */
    suspend fun insertFeedCategoriesToDatabase(
        sectionId: String,
        feedCategories: List<FeedCategory>,
    )

    /**
     * Retrieves sections and categories from database and converts them to feed sections and feed categories
     * Must call in [CoroutineScope]
     * @return List of feed sections and feed categories inside
     * */
    suspend fun retrieveFeedSectionsAndCategoriesFromDatabase(): List<FeedSection>

    /**
     * Retrieves categories from database by given `sectionId` and converts them to feed categories
     * Must call in [CoroutineScope]
     * @return List of feed categories which is belong to given `sectionId`
     * */
    suspend fun retrieveSectionAndItsCategoriesFromDatabase(sectionId: String): FeedSection

    /**
     * Retrieves introduction videos
     * Must call in [CoroutineScope]
     * */
    suspend fun retrieveIntroductions(): Flow<String>

    /**
     * Retrieves prices from network and from database if exists
     * Must call in [CoroutineScope]
     * */
    suspend fun retrievePrices(): Flow<Resource<List<TariffData>>>

    /**
     * Buys section by given [prices]
     * Must call in [CoroutineScope]
     * @param prices id of prices returned from [retrievePrices] and user selected
     * @return [Unit] if bought section
     * */
    suspend fun retrieveActiveSections(prices: List<String>): Flow<Resource<Unit>>

    /**
     * Fetches total amount of courses user want to buy
     * Must call in [CoroutineScope]
     * @param prices id of prices returned from [retrieveIntroductions] and user selected to buy
     * @return [BuyTotalResponse] if total responses return
     * */
    suspend fun fetchTotalAmount(prices: List<String>): Flow<Resource<BuyTotalResponseData>>

    /**
     * Creates payme buy receipt before buying in payment systems
     * Must call in [CoroutineScope]
     * @param prices id of prices returned from [retrieveIntroductions] and user selected to buy
     * @return [BuyWithPaymeResponse] if receipt create
     * */
    suspend fun buyWithPayme(prices: List<String>): Flow<Resource<BuyWithPaymeResponseData>>

    /**
     * Creates click buy receipt before buying in payment systems
     * Must call in [CoroutineScope]
     * @param prices id of prices returned from [retrieveIntroductions] and user selected to buy
     * @return [BuyWithClickResponseData] if receipt create
     * */
    suspend fun buyWithClick(prices: List<String>): Flow<Resource<BuyWithClickResponseData>>

    /**
     * Creates robo cassa buy receipt before buying in payment systems
     * Must call in [CoroutineScope]
     * @param prices id of prices returned from [retrieveIntroductions] and user selected to buy
     * @return [BuyWithRoboKassaResponse] if receipt create
     * */
    suspend fun buyWithRoboCassa(prices: List<String>): Flow<Resource<BuyWithRoboCassaResponseData>>

    /**
     * Retrieves background musics from local storage and remote by [categoryId]
     * @param categoryId id of a category that background musics will be retrieved
     * @return map of `String` to `File`, where name of bm is string and file of this name in folder
     * */
    suspend fun retrieveBMs(categoryId: String): Flow<Resource<List<PlayerBM>>>

    /**
     * Retrieves category audios by [categoryId]
     * @param categoryId id of a category which audios will be retrieved
     * @param filter if true filters audios by some specialities
     * */
    suspend fun retrieveCategoryAudios(
        categoryId: String,
        filter: Boolean = true,
    ): Flow<Resource<List<PlayerAudio>>>

    /**
     * Sets category as last listened by [categoryId]
     * Must call in [CoroutineScope]
     * @param categoryId id of category which will be set as last listened
     * */
    suspend fun setLastListened(categoryId: String)

    /**
     * Sets the real time retrieved from server
     * Must call in [CoroutineScope]
     * @param time time which retrieved from server
     * */
    suspend fun setRealTime(time: Long)

    /**
     * Save current audio
     * Must call in [CoroutineScope]
     * @param category which will be inserted
     * */
    suspend fun saveCurrentAudio(category: FeedCategory)

    /**
     * Deletes current audio
     * Must call in [CoroutineScope]
     * */
    suspend fun deleteCurrentAudio()

    /**
     * Calls when player released to copy temporary files to audios
     * Must call in [CoroutineScope]
     * */
    suspend fun setPlayerRelease()

    /**
     * Retrieves audios of files which is list type
     * Must call in [CoroutineScope]
     * @param categoryId id of category which will be retrieved
     * @return list of audios retrieved from database/network
     * */
    suspend fun retrieveSubCategories(categoryId: String): Flow<Resource<FeedCategory>>

    /**
     * Activates qr if not expired
     * Must call in [CoroutineScope]
     * @param qrCode code of qr returned from QR image
     * */
    suspend fun qr(qrCode: String? = null): Flow<Resource<Unit>>

    /**
     * Removes temporary qr in prefs
     * */
    fun deleteTemporaryQR()
}

class PodcastsRepositoryImpl @Inject constructor(
    private val apiHelper: RoviApiHelper,
    private val sectionsAndCategoriesDao: SectionsAndCategoriesDao,
    private var bmDao: BMDao,
    private var audioDao: AudioDao,
    private val priceDao: PriceDao,
    private var roviFiles: RoviFiles,
    private val prefs: RoviPrefs,
    override val minSectionsUpdateIntervalInMinutes: Int,
) : PodcastsRepository {
    private var lastUpdatedTime = prefs.configuration?.sectionsAndCategoriesLastUpdatedTime

    override suspend fun retrieveSections() = flow {
        // emits loading state
        emit(Resource.Loading())
        // sends sections retrieve request
        val sectionsResponse = apiHelper.retrieveSections()
        // emits success
        emit(Resource.Success(data = sectionsResponse.data))
    }

    override suspend fun retrieveCategories(sectionId: String) = flow {
        // emits categories by given sectionId if saved in database
        val sectionAndItsCategoriesInDatabase =
            retrieveSectionAndItsCategoriesFromDatabase(sectionId)

        if (sectionAndItsCategoriesInDatabase.categories.isNotEmpty())
            emit(Resource.Success(sectionAndItsCategoriesInDatabase))
        // returns if no update available
        val isUpdatable =
            checkSectionsAndCategoriesUpdate(listOf(sectionAndItsCategoriesInDatabase))
        if (!isUpdatable)
            return@flow
        // emits loading state
        emit(Resource.Loading())
        // sends categories retrieve request
        val categoriesResponse =
            apiHelper.retrieveCategories(sectionId)
        setRealTime(categoriesResponse.time)
        val mostListenedResponse = apiHelper.retrieveMostListenerCategories()
        val categories = categoriesResponse.data.map { category ->
            val subCategories = if (category.type == Category.LIST.type) {
                val subCategoriesResponse =
                    apiHelper.retrieveSubCategories(SubCategoriesRequest(category.id))
                subCategoriesResponse.data.subCategories.map { subCategory ->
                    val boughtDate = subCategoriesResponse.data.boughtDate
                    FeedSubCategory(
                        id = subCategory.id,
                        title = subCategory.title,
                        author = subCategory.author,
                        image = subCategory.image,
                        duration = subCategory.duration,
                        sectionTitle = subCategory.sectionTitle,
                        category = subCategory.category,
                        type = subCategory.type,
                        purchaseType = subCategory.purchaseType,
                        count = subCategory.count,
                        description = subCategory.description,
                        header = subCategory.header,
                        subHeader = subCategory.subHeader,
                        mostListened = subCategory.id in mostListenedResponse.data.map { it.id },
                        isBanner = subCategory.isBanner,
                        isActive = subCategory.purchaseType == Purchase.FREE.type,
                        lastListenedAt = sectionAndItsCategoriesInDatabase.categories.firstOrNull { it.id == subCategory.id }?.lastListenedAt,
                        boughtDate = boughtDate,
                        openingDay = subCategory.openingDay,
                        isOpen = isOpenable(boughtDate, subCategory.openingDay),
                        createdAt = subCategory.createdAt,
                        updatedAt = subCategory.updatedAt
                    )
                }
            } else emptyList()
            FeedCategory(
                id = category.id,
                title = category.title,
                image = category.image,
                duration = category.duration,
                section = category.section,
                sectionTitle = category.sectionTitle,
                type = category.type,
                purchaseType = category.purchaseType,
                count = category.count,
                description = category.description,
                header = category.header,
                subHeader = category.subHeader,
                subCategories = subCategories,
                mostListened = category.id in mostListenedResponse.data.map { it.id },
                isBanner = category.isBanner,
                isActive = category.purchaseType == Purchase.FREE.type,
                lastListenedAt = sectionAndItsCategoriesInDatabase.categories.firstOrNull { it.id == category.id }?.lastListenedAt,
                createdAt = category.createdAt,
                updatedAt = category.updatedAt
            )
        }
        // inserting categories
        insertFeedCategoriesToDatabase(sectionId, categories)
        // updating lastUpdateTime
        val configuration =
            RoviConfiguration(sectionsAndCategoriesLastUpdatedTime = System.currentTimeMillis())
        prefs.configuration =
            prefs.configuration?.copy(sectionsAndCategoriesLastUpdatedTime = System.currentTimeMillis())
                ?: configuration
        lastUpdatedTime = prefs.configuration?.sectionsAndCategoriesLastUpdatedTime
        // retrieves inserted categories from database and emits success
        val sectionAndItsCategories = retrieveSectionAndItsCategoriesFromDatabase(sectionId)
        emit(Resource.Success(sectionAndItsCategories))
    }

    override suspend fun retrieveSectionsAndCategories(
        ignoreCache: Boolean,
    ): Flow<Resource<List<FeedSection>>> = flow {
        // emits sections and categories if saved in database
        val sectionsAndCategoriesInDatabase = retrieveFeedSectionsAndCategoriesFromDatabase()
        if (sectionsAndCategoriesInDatabase.isNotEmpty() && !ignoreCache)
            emit(Resource.Success(sectionsAndCategoriesInDatabase))
        // returns if no update available
        val isUpdatable = checkSectionsAndCategoriesUpdate(sectionsAndCategoriesInDatabase)
        if (!isUpdatable && !ignoreCache)
            return@flow
        // emits loading state
        emit(Resource.Loading())
        // creates feed-sections to fill it in response
        val sections = arrayListOf<FeedSection?>()
        // sends sections retrieve request
        val sectionsResponse = apiHelper.retrieveSections()
        val activeSectionsResponse = apiHelper.retrieveActiveSections()
        sectionsResponse.data.forEach { section ->
            val active = activeSectionsResponse.data.firstOrNull { it.section == section.id }
            sections.add(
                FeedSection(
                    id = section.id,
                    title = section.title,
                    image = section.image,
                    isActive = section.isActive,
                    isMain = section.isMain,
                    categories = emptyList(),
                    boughtDate = active?.boughtDate,
                    expireDate = active?.expireDate
                )
            )
            // sends categories retrieve request
            val categoriesResponse =
                apiHelper.retrieveCategories(section.id)
            setRealTime(categoriesResponse.time)
            val mostListenedResponse = apiHelper.retrieveMostListenerCategories()
            val categories = categoriesResponse.data.map { category ->
                val subCategories = if (category.type == Category.LIST.type) {
                    val subCategoriesResponse =
                        apiHelper.retrieveSubCategories(SubCategoriesRequest(category.id))
                    subCategoriesResponse.data.subCategories.map { subCategory ->
                        val boughtDate = subCategoriesResponse.data.boughtDate
                        FeedSubCategory(
                            id = subCategory.id,
                            title = subCategory.title,
                            author = subCategory.author,
                            image = subCategory.image,
                            duration = subCategory.duration,
                            sectionTitle = subCategory.sectionTitle,
                            category = subCategory.category,
                            type = subCategory.type,
                            purchaseType = subCategory.purchaseType,
                            count = subCategory.count,
                            description = subCategory.description,
                            header = subCategory.header,
                            subHeader = subCategory.subHeader,
                            mostListened = subCategory.id in mostListenedResponse.data.map { it.id },
                            isBanner = subCategory.isBanner,
                            isActive = section.isActive or (category.purchaseType == Purchase.FREE.type),
                            lastListenedAt = sectionsAndCategoriesInDatabase.flatMap { it.categories }
                                .firstOrNull { it.id == subCategory.id }?.lastListenedAt,
                            boughtDate = boughtDate,
                            openingDay = subCategory.openingDay,
                            isOpen = isOpenable(boughtDate, subCategory.openingDay),
                            createdAt = subCategory.createdAt,
                            updatedAt = subCategory.updatedAt
                        )
                    }
                } else emptyList()

                FeedCategory(
                    id = category.id,
                    title = category.title,
                    image = category.image,
                    duration = category.duration,
                    section = category.section,
                    sectionTitle = category.sectionTitle,
                    type = category.type,
                    purchaseType = category.purchaseType,
                    count = category.count,
                    description = category.description,
                    header = category.header,
                    subHeader = category.subHeader,
                    subCategories = subCategories,
                    mostListened = category.id in mostListenedResponse.data.map { it.id },
                    isBanner = category.isBanner,
                    isActive = section.isActive or (category.purchaseType == Purchase.FREE.type),
                    lastListenedAt = sectionsAndCategoriesInDatabase.flatMap { it.categories }
                        .firstOrNull { it.id == category.id }?.lastListenedAt,
                    createdAt = category.createdAt,
                    updatedAt = category.updatedAt
                )
            }
            val last = sections.lastOrNull()
            sections[sections.lastIndex] = last?.copy(categories = categories)
        }
        // inserting feed sections which has not empty categories
        insertFeedSectionsAndCategoriesToDatabase(
            sections.filterNotNull().filterNot { it.categories.isEmpty() }
        )
        // updating lastUpdatedTime
        val configuration =
            RoviConfiguration(sectionsAndCategoriesLastUpdatedTime = System.currentTimeMillis())
        prefs.configuration =
            prefs.configuration?.copy(sectionsAndCategoriesLastUpdatedTime = System.currentTimeMillis())
                ?: configuration
        lastUpdatedTime = prefs.configuration?.sectionsAndCategoriesLastUpdatedTime
        // retrieves inserted sections and emits success
        val sectionsAndCategories = retrieveFeedSectionsAndCategoriesFromDatabase()
        emit(Resource.Success(sectionsAndCategories))
    }

    override suspend fun insertFeedSectionsAndCategoriesToDatabase(feedSections: List<FeedSection>) {
        // clearing database
        sectionsAndCategoriesDao.clearSections()
        sectionsAndCategoriesDao.clearCategories()
        sectionsAndCategoriesDao.clearSubCategories()
        // filtering sections with no empty category
        feedSections.filterNot { it.categories.isEmpty() }.forEach { feedSection ->
            // mapping feed section to section entity
            val section = SectionEntity(
                id = feedSection.id,
                title = feedSection.title,
                image = feedSection.image,
                isActive = feedSection.isActive,
                isMain = feedSection.isMain,
                boughtDate = feedSection.boughtDate,
                expireDate = feedSection.expireDate
            )
            // inserting section
            sectionsAndCategoriesDao.insertSection(section)

            // mapping feed section categories to category entities
            val categories = feedSection.categories.map { feedCategory ->
                // insert sub categories
                val subCategories = feedCategory.subCategories.map { feedSubCategory ->
                    SubCategoryEntity(
                        id = feedSubCategory.id,
                        title = feedSubCategory.title,
                        author = feedSubCategory.author,
                        image = feedSubCategory.image,
                        duration = feedSubCategory.duration,
                        sectionTitle = feedSubCategory.sectionTitle,
                        category = feedSubCategory.category,
                        type = feedSubCategory.type,
                        purchaseType = feedSubCategory.purchaseType,
                        count = feedSubCategory.count,
                        description = feedSubCategory.description,
                        header = feedSubCategory.header,
                        subHeader = feedSubCategory.subHeader,
                        mostListened = feedSubCategory.mostListened,
                        isBanner = feedSubCategory.isBanner,
                        isActive = feedSubCategory.isActive,
                        lastListenedAt = feedSubCategory.lastListenedAt,
                        boughtDate = feedSubCategory.boughtDate,
                        openingDay = feedSubCategory.openingDay,
                        isOpen = isOpenable(feedSubCategory.boughtDate, feedSubCategory.openingDay),
                        createdAt = feedSubCategory.createdAt,
                        updatedAt = feedSubCategory.updatedAt
                    )
                }
                if (feedCategory.isActive)
                    sectionsAndCategoriesDao.insertSubCategories(subCategories)

                CategoryEntity(
                    id = feedCategory.id,
                    title = feedCategory.title,
                    image = feedCategory.image,
                    duration = feedCategory.duration,
                    section = feedCategory.section,
                    sectionTitle = feedCategory.sectionTitle,
                    type = feedCategory.type,
                    purchaseType = feedCategory.purchaseType,
                    count = feedCategory.count,
                    description = feedCategory.description,
                    header = feedCategory.header,
                    subHeader = feedCategory.subHeader,
                    mostListened = feedCategory.mostListened,
                    isBanner = feedCategory.isBanner,
                    isActive = feedCategory.isActive,
                    lastListenedAt = feedCategory.lastListenedAt,
                    createdAt = feedCategory.createdAt,
                    updatedAt = feedCategory.updatedAt
                )
            }
            // inserting categories
            sectionsAndCategoriesDao.insertCategories(categories)
        }
    }

    override suspend fun insertFeedCategoriesToDatabase(
        sectionId: String,
        feedCategories: List<FeedCategory>,
    ) {
        // clearing categories from database
        sectionsAndCategoriesDao.clearCategoriesBySection(sectionId)
        val section = sectionsAndCategoriesDao.retrieveSection(sectionId) ?: return
        sectionsAndCategoriesDao.insertCategories(feedCategories.map { category ->
            // insert sub categories
            val subCategories = category.subCategories.map { feedSubCategory ->
                SubCategoryEntity(
                    id = feedSubCategory.id,
                    title = feedSubCategory.title,
                    author = feedSubCategory.author,
                    image = feedSubCategory.image,
                    duration = feedSubCategory.duration,
                    sectionTitle = feedSubCategory.sectionTitle,
                    category = feedSubCategory.category,
                    type = feedSubCategory.type,
                    purchaseType = feedSubCategory.purchaseType,
                    count = feedSubCategory.count,
                    description = feedSubCategory.description,
                    header = feedSubCategory.header,
                    subHeader = feedSubCategory.subHeader,
                    mostListened = feedSubCategory.mostListened,
                    isBanner = feedSubCategory.isBanner,
                    isActive = feedSubCategory.isActive,
                    lastListenedAt = feedSubCategory.lastListenedAt,
                    boughtDate = feedSubCategory.boughtDate,
                    openingDay = feedSubCategory.openingDay,
                    isOpen = isOpenable(feedSubCategory.boughtDate, feedSubCategory.openingDay),
                    createdAt = feedSubCategory.createdAt,
                    updatedAt = feedSubCategory.updatedAt
                )
            }
            if (category.isActive)
                sectionsAndCategoriesDao.insertSubCategories(subCategories)

            CategoryEntity(
                id = category.id,
                title = category.title,
                image = category.image,
                duration = category.duration,
                section = category.section,
                sectionTitle = category.sectionTitle,
                type = category.type,
                purchaseType = category.purchaseType,
                count = category.count,
                description = category.description,
                header = category.header,
                subHeader = category.subHeader,
                mostListened = category.mostListened,
                isBanner = category.isBanner,
                isActive = section.isActive or (category.purchaseType == Purchase.FREE.type),
                lastListenedAt = category.lastListenedAt,
                createdAt = category.createdAt,
                updatedAt = category.updatedAt
            )
        })
    }

    override suspend fun retrieveFeedSectionsAndCategoriesFromDatabase(): List<FeedSection> {
        // creating empty feed sections, which will be filled and returned
        val feedSections = arrayListOf<FeedSection?>()

        // retrieve sections from database
        val sections = sectionsAndCategoriesDao.retrieveSections()
        sections.forEach { section ->
            // adding section to feed sections
            feedSections.add(
                FeedSection(
                    id = section.id,
                    title = section.title,
                    image = section.image,
                    isActive = section.isActive,
                    isMain = section.isMain,
                    boughtDate = section.boughtDate,
                    expireDate = section.expireDate,
                    categories = emptyList()
                )
            )
            // retrieving categories by sectionId from database
            val categories =
                sectionsAndCategoriesDao.retrieveCategoriesBySection(section = section.id)
                    .sortedBy { it.title }

            // creating feed categories from mapping database retrieved categories
            val feedCategories = categories.map { category ->
                val subCategories =
                    sectionsAndCategoriesDao.retrieveSubCategoriesByCategory(category.id)

                val feedSubCategories = subCategories.map { subCategory ->
                    FeedSubCategory(
                        id = subCategory.id,
                        title = subCategory.title,
                        author = subCategory.author,
                        image = subCategory.image,
                        duration = subCategory.duration,
                        sectionTitle = subCategory.sectionTitle,
                        category = subCategory.category,
                        type = subCategory.type,
                        purchaseType = subCategory.purchaseType,
                        count = subCategory.count,
                        description = subCategory.description,
                        header = subCategory.header,
                        subHeader = subCategory.subHeader,
                        mostListened = subCategory.mostListened,
                        isBanner = subCategory.isBanner,
                        isActive = category.isActive,
                        lastListenedAt = subCategory.lastListenedAt,
                        boughtDate = subCategory.boughtDate,
                        openingDay = subCategory.openingDay,
                        isOpen = isOpenable(subCategory.boughtDate, subCategory.openingDay),
                        createdAt = subCategory.createdAt,
                        updatedAt = subCategory.updatedAt
                    )
                }

                FeedCategory(
                    id = category.id,
                    title = category.title,
                    image = category.image,
                    duration = category.duration,
                    section = category.section,
                    sectionTitle = category.sectionTitle,
                    type = category.type,
                    purchaseType = category.purchaseType,
                    count = category.count,
                    description = category.description,
                    header = category.header,
                    subHeader = category.subHeader,
                    subCategories = feedSubCategories,
                    mostListened = category.mostListened,
                    isBanner = category.isBanner,
                    isActive = category.isActive,
                    lastListenedAt = category.lastListenedAt,
                    createdAt = category.createdAt,
                    updatedAt = category.updatedAt
                )
            }

            val last = feedSections.lastOrNull()
            feedSections[feedSections.lastIndex] = last?.copy(categories = feedCategories)
        }

        return feedSections.filterNotNull().filterNot { it.categories.isEmpty() }
    }

    override suspend fun retrieveSectionAndItsCategoriesFromDatabase(sectionId: String): FeedSection {
        val categories =
            sectionsAndCategoriesDao.retrieveCategoriesBySection(sectionId).map { category ->
                val subCategories =
                    sectionsAndCategoriesDao.retrieveSubCategoriesByCategory(category.id)
                val feedSubCategories = subCategories.map { subCategory ->
                    FeedSubCategory(
                        id = subCategory.id,
                        title = subCategory.title,
                        author = subCategory.author,
                        image = subCategory.image,
                        duration = subCategory.duration,
                        sectionTitle = subCategory.sectionTitle,
                        category = subCategory.category,
                        type = subCategory.type,
                        purchaseType = subCategory.purchaseType,
                        count = subCategory.count,
                        description = subCategory.description,
                        header = subCategory.header,
                        subHeader = subCategory.subHeader,
                        mostListened = subCategory.mostListened,
                        isBanner = subCategory.isBanner,
                        isActive = category.isActive,
                        lastListenedAt = subCategory.lastListenedAt,
                        boughtDate = subCategory.boughtDate,
                        openingDay = subCategory.openingDay,
                        isOpen = isOpenable(subCategory.boughtDate, subCategory.openingDay),
                        createdAt = subCategory.createdAt,
                        updatedAt = subCategory.updatedAt
                    )
                }

                FeedCategory(
                    id = category.id,
                    title = category.title,
                    image = category.image,
                    duration = category.duration,
                    section = category.section,
                    sectionTitle = category.sectionTitle,
                    type = category.type,
                    purchaseType = category.purchaseType,
                    count = category.count,
                    description = category.description,
                    header = category.header,
                    subHeader = category.subHeader,
                    subCategories = feedSubCategories,
                    mostListened = category.mostListened,
                    isBanner = category.isBanner,
                    isActive = category.isActive,
                    lastListenedAt = category.lastListenedAt,
                    createdAt = category.createdAt,
                    updatedAt = category.updatedAt
                )
            }.sortedBy { it.title }
        val sectionEntity = sectionsAndCategoriesDao.retrieveSection(sectionId)
            ?: throw SectionByIdNotFoundException(
                SectionsExceptionData(exception = SectionsExceptions.SECTION_BY_ID_NOT_FOUND_EXCEPTION)
            )

        return FeedSection(
            id = sectionEntity.id,
            title = sectionEntity.title,
            image = sectionEntity.image,
            isActive = sectionEntity.isActive,
            isMain = sectionEntity.isMain,
            boughtDate = sectionEntity.boughtDate,
            expireDate = sectionEntity.expireDate,
            categories = categories
        )
    }

    private fun <T> checkSectionsAndCategoriesUpdate(items: List<T>): Boolean {
        val updateIntervalInMillis = minSectionsUpdateIntervalInMinutes * 60 * 1000

        val currentTime = System.currentTimeMillis()

        if (currentTime - (lastUpdatedTime ?: 0) >= updateIntervalInMillis)
            return true
        if (items.isEmpty())
            return true

        return false
    }

    override suspend fun retrieveIntroductions() = flow {
        val introductionInPrefs = prefs.introduction
        if (introductionInPrefs != null)
            emit(introductionInPrefs)
        // send post request to retrieve introductions
        val introductions = apiHelper.retrieveIntroductions()
        // saves to prefs
        prefs.introduction =
            introductions.data.first { it.type == ResourceLinks.INTRODUCTION.title }.link
        prefs.settingsLinks =
            SettingsLinks(introductions.data.map { SettingsLinkData(it.type, it.link) })
        // emits introductions first
        if (introductionInPrefs == null)
            emit(introductions.data.first().link)
    }

    override suspend fun retrievePrices() = flow {
        // emits prices if exists in database
        val tariffs = retrieveTariffsFromDatabase()
        if (tariffs.isNotEmpty())
            emit(Resource.Success(tariffs))
        // emits loading state
        if (tariffs.isEmpty())
            emit(Resource.Loading())
        // sends retrieve prices request
        val pricesResponse = apiHelper.retrievePrices()
        // inserts prices to database
        insertPricesToDatabase(pricesResponse.data)
        // retrieves inserted prices from database and emits them if database was empty
        val tariffsAfterUpdate = retrieveTariffsFromDatabase()
        if (tariffs.isEmpty())
            emit(Resource.Success(tariffsAfterUpdate))
    }

    private suspend fun insertPricesToDatabase(prices: List<PricesData>) {
        priceDao.clearPrices()
        prices.forEach { pricesData ->
            pricesData.prices.forEach { price ->
                priceDao.insertPrice(
                    PriceEntity(
                        id = price.id,
                        cost = price.cost,
                        duration = price.duration,
                        section = price.section,
                        title = pricesData.title
                    )
                )
            }
        }
    }

    private suspend fun retrieveTariffsFromDatabase(): List<TariffData> {
        val tariffs = arrayListOf<TariffData>()

        val sections = sectionsAndCategoriesDao.retrieveSections()
        sections.forEach { section ->
            val prices = priceDao.retrievePricesBySection(section.id)
            val oneMonth = prices.getOrNull(0)
            val threeMonth = prices.getOrNull(1)
            val sixMonth = prices.getOrNull(2)
            tariffs.add(
                TariffData(
                    title = prices.firstOrNull()?.title ?: "",
                    oneMonthTariff = if (oneMonth == null) null else Tariff(
                        id = oneMonth.id,
                        price = oneMonth.cost,
                        duration = oneMonth.duration
                    ),
                    threeMonthTariff = if (threeMonth == null) null else Tariff(
                        id = threeMonth.id,
                        price = threeMonth.cost,
                        duration = threeMonth.duration
                    ),
                    sixMonthTariff = if (sixMonth == null) null else Tariff(
                        id = sixMonth.id,
                        price = sixMonth.cost,
                        duration = sixMonth.duration
                    ),
                    section = section.id
                )
            )
        }

        return tariffs.filterNot { it.oneMonthTariff == null && it.threeMonthTariff == null && it.sixMonthTariff == null }
    }

    override suspend fun retrieveActiveSections(prices: List<String>) = flow {
        // TODO: update this method name and args
        // emits loading state
        emit(Resource.Loading())
        // creates BuyRequest and sends it
//        apiHelper.buy(BuyRequest(prices))
        // retrieves active sections
        val activeSections = apiHelper.retrieveActiveSections()
        insertActiveSections(activeSections.data)
        // emits success
        emit(Resource.Success(Unit))
    }

    private suspend fun insertActiveSections(sections: List<ActiveSectionData>) {
        sections.forEach { section ->
            val sectionInDB = sectionsAndCategoriesDao.retrieveSection(section.section)
            sectionInDB?.let {
                sectionsAndCategoriesDao.updateSection(
                    sectionInDB.copy(
                        isActive = true,
                        boughtDate = section.boughtDate,
                        expireDate = section.expireDate
                    )
                )
                val categoriesInDB =
                    sectionsAndCategoriesDao.retrieveCategoriesBySection(sectionInDB.id)
                categoriesInDB.forEach { category ->
                    sectionsAndCategoriesDao.updateCategory(
                        category.copy(
                            isActive = true
                        )
                    )
                }
            }
        }
    }

    override suspend fun fetchTotalAmount(prices: List<String>) = flow {
        // emits loading state
        emit(Resource.Loading())
        // creates BuyRequest and sends it
        val response = apiHelper.fetchTotalAmount(BuyRequest(prices))
        // emits success
        emit(Resource.Success(response.data))
    }

    override suspend fun buyWithPayme(prices: List<String>) = flow {
        // emits loading state
        emit(Resource.Loading())
        // creates BuyRequest and sends it
        val response = apiHelper.buyWithPayme(BuyRequest(prices))
        // emits success
        emit(Resource.Success(response.data))
    }

    override suspend fun buyWithClick(prices: List<String>) = flow {
        // emits loading state
        emit(Resource.Loading())
        // creates BuyRequest and sends it
        val response = apiHelper.buyWithClick(BuyRequest(prices))
        // emits success
        emit(Resource.Success(response.data))
    }

    override suspend fun buyWithRoboCassa(prices: List<String>) = flow {
        // emits loading state
        emit(Resource.Loading())
        // creates BuyRequest and sends it
        val response = apiHelper.buyWithRoboCassa(BuyRequest(prices))
        // emits success
        emit(Resource.Success(response.data))
    }

    override suspend fun retrieveBMs(categoryId: String) = flow {
        // emits background musics if saved in database and files
        val bmsInDatabase = retrieveBMsFromDatabase(categoryId)
        emit(Resource.Success(bmsInDatabase))
        // emits loading state
        if (bmsInDatabase.isEmpty())
            emit(Resource.Loading())
        // sends background musics by category retrieve request
        val bmResponse = apiHelper.retrieveBackgroundMusics(categoryId)
        // inserts bms to database
        insertBMsToStorage(categoryId, bmResponse.data)
        // retrieves background musics from local storage
        val bmsAfterDownload = retrieveBMsFromDatabase(categoryId)
        if (bmsInDatabase.isEmpty())
            emit(Resource.Success(bmsAfterDownload))
        // downloads bms and emits them one by one
        val bmsResponseInDatabase =
            bmResponse.data.filter { bm -> bm.id in bmsAfterDownload.map { it.id } }
        downloadBMs(bmsResponseInDatabase)
    }

    private suspend fun insertBMsToStorage(categoryId: String, bms: List<BackgroundMusicData>) {
        bmDao.clearBMs()
        bms.forEach { bm ->
            bmDao.insertBM(
                BMEntity(
                    id = bm.id,
                    image = bm.image,
                    audio = bm.link,
                    category = categoryId
                )
            )
        }
    }

    private suspend fun FlowCollector<Resource<List<PlayerBM>>>.downloadBMs(bms: List<BackgroundMusicData>) {
        val bmFileIds = roviFiles.retrieveBMs(bms.map { it.id })
        bms.forEach { bm ->
            if (bm.id !in bmFileIds) {
                val bmIN = apiHelper.streamFile(bm.link.separateFromBase())
                roviFiles.saveBM(bm.id, bmIN.byteStream())
                val playerBM = PlayerBM(
                    id = bm.id,
                    image = bm.image,
                    state = BMState.LOADED
                )
                emit(Resource.Success(listOf(playerBM)))
            }
        }
    }

    private suspend fun retrieveBMsFromDatabase(categoryId: String): List<PlayerBM> {
        val bmsInDatabase = bmDao.retrieveBMsByCategory(categoryId)
        val bmsMap = roviFiles.retrieveBMs(bmsInDatabase.map { it.id })
        return bmsInDatabase.map { bm ->
            PlayerBM(
                id = bm.id,
                image = bm.image,
                state = if (bmsMap[bm.id] == null) BMState.LOADING else BMState.LOADED,
                file = bmsMap[bm.id]
            )
        }
    }

    override suspend fun retrieveCategoryAudios(
        categoryId: String,
        filter: Boolean,
    ): Flow<Resource<List<PlayerAudio>>> = flow {
        // emits audios if exists in database
        val audiosInDatabase = retrieveAudiosFromDatabase(categoryId/*, filter*/)
        if (audiosInDatabase.isNotEmpty())
            emit(Resource.Success(audiosInDatabase))
        // emits loading state if audios in database is empty
        if (audiosInDatabase.isEmpty())
            emit(Resource.Loading())
        // creating player audio to fill and insert after retrieve files
        val playerAudios = arrayListOf<PlayerAudio>()
        // sends retrieve category files request and filter them by audio
        val categoryFilesResponse = apiHelper.retrieveCategoryFiles(categoryId)
        val categoryAudios = categoryFilesResponse.data.filter { it.type == FileType.AUDIO.type }
        categoryAudios.forEach { categoryAudio ->
            // sends retrieve file request by its id(here we can retrieve url of file)
            val audioResponse = apiHelper.retrieveFile(categoryAudio.id)
            val audio = audioResponse.data

            playerAudios.add(
                PlayerAudio(
                    id = audio.id,
                    title = audio.title,
                    image = audio.image,
                    duration = audio.duration,
                    category = audio.category,
                    categoryTitle = audio.categoryTitle,
                    categoryImage = null,
                    author = audio.author,
                    url = audio.link,
                    lastPlayedPosition = null,
                    createdAt = audio.createdAt,
                    updatedAt = audio.updatedAt
                )
            )
        }
        // inserting retrieved audios to database
        insertPlayerAudiosToDatabase(categoryId, playerAudios)
        // retrieving audios from database and emits success if audios in database was empty
        val audios = retrieveAudiosFromDatabase(categoryId/*, filter*/)
        if (audiosInDatabase.isEmpty())
            emit(Resource.Success(audios))
        // downloads audios
        downloadAudios(audios)
    }

    private suspend fun insertPlayerAudiosToDatabase(
        categoryId: String,
        audios: List<PlayerAudio>,
        /*updateFiles: Boolean = false,*/
    ) {
        // updates audio files if files changed, before insert
/*        if (updateFiles) {
            val audiosInDatabase = audioDao.retrieveAudiosByCategory(categoryId)
            updateAudioFiles(audiosInDatabase, audios)
        }*/

        audioDao.clearAudiosByCategory(categoryId)
        audios.forEach { audio ->
            audioDao.insertAudio(
                AudioEntity(
                    id = audio.id,
                    title = audio.title,
                    image = audio.image,
                    duration = audio.duration,
                    category = audio.category,
                    categoryTitle = audio.categoryTitle,
                    author = audio.author,
                    url = audio.url,
                    lastPlayedPosition = audio.lastPlayedPosition,
                    createdAt = audio.createdAt,
                    updatedAt = audio.updatedAt
                )
            )
        }
    }

    private suspend fun downloadAudios(audios: List<PlayerAudio>) {
        val audioFileIds = roviFiles.retrieveAudios(audios.map { it.id.localeName(prefs.language) })
        audios.forEach { audio ->
            if (audio.id.localeName(prefs.language) !in audioFileIds && audio.url != null) {
                val responseBody = apiHelper.streamFile(audio.url)
                roviFiles.saveAudio(
                    audio.id.localeName(prefs.language),
                    responseBody.byteStream(),
                    responseBody.contentLength()
                )
            }
        }
    }

    private suspend fun retrieveAudiosFromDatabase(
        categoryId: String,
        /*filter: Boolean = true,*/
    ): List<PlayerAudio> {
        val categoryInDatabase = sectionsAndCategoriesDao.retrieveCategory(categoryId)
        val audios = audioDao.retrieveAudiosByCategory(categoryId)
        val audiosMap = roviFiles.retrieveAudios(audios.map { it.id.localeName(prefs.language) })
        return audios.map { audio ->
            PlayerAudio(
                id = audio.id,
                title = audio.title,
                image = audio.image,
                duration = audio.duration,
                category = audio.category,
                categoryTitle = audio.categoryTitle,
                author = audio.author,
                categoryImage = categoryInDatabase?.image,
                url = audio.url,
                lastPlayedPosition = audio.lastPlayedPosition,
                file = audiosMap[audio.id.localeName(prefs.language)],
                createdAt = audio.createdAt,
                updatedAt = audio.updatedAt
            )
        }/*.filter { if (filter) isOpenable(it.openingDay) else true }*/
    }

    private fun String.localeName(language: String): String {
        return "$this-$language"
    }

    override suspend fun setLastListened(categoryId: String) {
        val categoryInDatabase = sectionsAndCategoriesDao.retrieveCategory(categoryId)
        categoryInDatabase?.let { category ->
            val millis = System.currentTimeMillis()
            sectionsAndCategoriesDao.insertCategory(category.copy(lastListenedAt = millis))
        }
    }

    override suspend fun setRealTime(time: Long) {
        prefs.configuration =
            prefs.configuration?.copy(time = time) ?: RoviConfiguration(time = time)
    }

    override suspend fun saveCurrentAudio(category: FeedCategory) {
        prefs.currentAudio = category
    }

    override suspend fun deleteCurrentAudio() {
        prefs.currentAudio = null
    }

    override suspend fun setPlayerRelease() {
        roviFiles.emptyTemporary()
    }

    override suspend fun retrieveSubCategories(categoryId: String) = flow {
        // emits sub categories by given categoryId if saved in database
        val categoryAndItsSubsInDatabase = retrieveCategoryAndItsSubs(categoryId)

        if (categoryAndItsSubsInDatabase.subCategories.isNotEmpty())
            emit(Resource.Success(categoryAndItsSubsInDatabase))

        // emits loading state if database empty
        if (categoryAndItsSubsInDatabase.subCategories.isEmpty())
            emit(Resource.Loading())
        // sends categories retrieve request
        val subCategoriesResponse =
            apiHelper.retrieveSubCategories(SubCategoriesRequest(categoryId))
        setRealTime(subCategoriesResponse.time)
        val subCategories = subCategoriesResponse.data.subCategories.map { subCategory ->
            val boughtDate = subCategoriesResponse.data.boughtDate
            FeedSubCategory(
                id = subCategory.id,
                title = subCategory.title,
                author = subCategory.author,
                image = subCategory.image,
                duration = subCategory.duration,
                sectionTitle = subCategory.sectionTitle,
                category = subCategory.category,
                type = subCategory.type,
                purchaseType = subCategory.purchaseType,
                count = subCategory.count,
                description = subCategory.description,
                header = subCategory.header,
                subHeader = subCategory.subHeader,
                mostListened = false,
                isBanner = subCategory.isBanner,
                isActive = categoryAndItsSubsInDatabase.isActive,
                lastListenedAt = categoryAndItsSubsInDatabase.subCategories.firstOrNull { it.id == subCategory.id }?.lastListenedAt,
                boughtDate = boughtDate,
                openingDay = subCategory.openingDay,
                isOpen = isOpenable(boughtDate, subCategory.openingDay),
                createdAt = subCategory.createdAt,
                updatedAt = subCategory.updatedAt
            )
        }
        // inserting sub categories
        insertFeedSubCategoriesToDatabase(categoryId, subCategories)
        // updating lastUpdateTime
        val configuration =
            RoviConfiguration(sectionsAndCategoriesLastUpdatedTime = System.currentTimeMillis())
        prefs.configuration =
            prefs.configuration?.copy(sectionsAndCategoriesLastUpdatedTime = System.currentTimeMillis())
                ?: configuration
        lastUpdatedTime = prefs.configuration?.sectionsAndCategoriesLastUpdatedTime
        // retrieves inserted categories from database and emits success
        val categoryAndItsSubs = retrieveCategoryAndItsSubs(categoryId)
        // emits succes if database was empty
        if (categoryAndItsSubsInDatabase.subCategories.isEmpty())
            emit(Resource.Success(categoryAndItsSubs))
    }

    private suspend fun retrieveCategoryAndItsSubs(categoryId: String): FeedCategory {
        val category = sectionsAndCategoriesDao.retrieveCategory(categoryId)
            ?: throw Exception() // TODO: set category not found exception
        val subCategories = sectionsAndCategoriesDao.retrieveSubCategoriesByCategory(categoryId)

        val feedSubCategories = subCategories.map { subCategory ->
            FeedSubCategory(
                id = subCategory.id,
                title = subCategory.title,
                author = subCategory.author,
                image = subCategory.image,
                duration = subCategory.duration,
                sectionTitle = subCategory.sectionTitle,
                category = subCategory.category,
                type = subCategory.type,
                purchaseType = subCategory.purchaseType,
                count = subCategory.count,
                description = subCategory.description,
                header = subCategory.header,
                subHeader = subCategory.subHeader,
                mostListened = subCategory.mostListened,
                isBanner = subCategory.isBanner,
                isActive = category.isActive,
                lastListenedAt = subCategory.lastListenedAt,
                boughtDate = subCategory.boughtDate,
                openingDay = subCategory.openingDay,
                isOpen = isOpenable(subCategory.boughtDate, subCategory.openingDay),
                createdAt = subCategory.createdAt,
                updatedAt = subCategory.updatedAt
            )
        }

        return FeedCategory(
            id = category.id,
            title = category.title,
            image = category.image,
            duration = category.duration,
            section = category.section,
            sectionTitle = category.sectionTitle,
            type = category.type,
            purchaseType = category.purchaseType,
            count = category.count,
            description = category.description,
            header = category.header,
            subHeader = category.subHeader,
            subCategories = feedSubCategories,
            mostListened = category.mostListened,
            isBanner = category.isBanner,
            isActive = category.isActive,
            lastListenedAt = category.lastListenedAt,
            createdAt = category.createdAt,
            updatedAt = category.updatedAt
        )
    }

    private suspend fun insertFeedSubCategoriesToDatabase(
        categoryId: String,
        feedSubCategories: List<FeedSubCategory>,
    ) {
        // clearing sub categories from database
        sectionsAndCategoriesDao.clearSubCategoriesByCategory(categoryId)
        val category = sectionsAndCategoriesDao.retrieveCategory(categoryId) ?: return
        if (!category.isActive) return
        sectionsAndCategoriesDao.insertSubCategories(feedSubCategories.map { feedSubCategory ->
            SubCategoryEntity(
                id = feedSubCategory.id,
                title = feedSubCategory.title,
                author = feedSubCategory.author,
                image = feedSubCategory.image,
                duration = feedSubCategory.duration,
                sectionTitle = feedSubCategory.sectionTitle,
                category = feedSubCategory.category,
                type = feedSubCategory.type,
                purchaseType = feedSubCategory.purchaseType,
                count = feedSubCategory.count,
                description = feedSubCategory.description,
                header = feedSubCategory.header,
                subHeader = feedSubCategory.subHeader,
                mostListened = feedSubCategory.mostListened,
                isBanner = feedSubCategory.isBanner,
                isActive = category.isActive,
                lastListenedAt = feedSubCategory.lastListenedAt,
                boughtDate = feedSubCategory.boughtDate,
                openingDay = feedSubCategory.openingDay,
                isOpen = isOpenable(feedSubCategory.boughtDate, feedSubCategory.openingDay),
                createdAt = feedSubCategory.createdAt,
                updatedAt = feedSubCategory.updatedAt
            )
        })
    }

    private fun isOpenable(boughtDate: Long?, openingDay: Int): Boolean {
        return boughtDate?.let {
            val opening = Calendar.getInstance()
            opening.time = Date(it)
            opening[Calendar.DATE] = opening[Calendar.DATE] + openingDay
            val time = Calendar.getInstance(TimeZone.getDefault())
            val today = prefs.configuration?.time ?: System.currentTimeMillis()
            time.time = Date(today)

            opening.before(time)
//            it <= prefs.configuration?.time ?: System.currentTimeMillis()
        } ?: false
    }

    override suspend fun qr(qrCode: String?) = flow {
        val qrInPrefs = prefs.qr
        if (qrCode == null && qrInPrefs == null) return@flow
        // emits loading state
        emit(Resource.Loading())
        // creates QR Request and sends it
        apiHelper.qr(QRRequest(qrCode ?: qrInPrefs ?: return@flow))
        // deletes temporary qr if exists
        deleteTemporaryQR()
        // retrieves active sections
        val activeSections = apiHelper.retrieveActiveSections()
        insertActiveSections(activeSections.data)
        // emits success
        emit(Resource.Success(Unit))
    }

    override fun deleteTemporaryQR() {
        prefs.qr = null
    }
}