package uz.invan.rovitalk.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import uz.invan.rovitalk.data.models.network.requests.*
import uz.invan.rovitalk.data.models.network.responses.*

interface RoviApi {
    /**
     * call's to authenticate user to system
     * sends [POST] request to /login route
     * @param loginRequest Login Request params to authenticate user to system
     * @return [LoginResponse] on success
     * */
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    /**
     * call's to authorize user to system
     * sends [POST] request to /register route
     * @param registerRequest Register Request params to authorize user to system with firstName, lastName, phone
     * @return [RegisterRequest] on success
     * */
    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterRequest

    /**
     * call's to verify `otp` sent user and set as logged in
     * sends [POST] request to /verify route
     * @param verifyRequest Verify Request params to verify user enter otp and check correctness
     * @return [VerifyResponse] on success
     * */
    @POST("verify")
    suspend fun verify(@Body verifyRequest: VerifyRequest): VerifyResponse

    /**
     * call's to edit users profile with given parameters
     * sends [POST] request to /update-profile route
     * @param editProfileRequest Edit Profile Request params which contains user data
     * @return [EditProfileResponse] on success
     * */
    @POST("update-profile")
    suspend fun editProfile(@Body editProfileRequest: EditProfileRequest): EditProfileResponse

    /**
     * call's to upload image file
     * sends [POST] request to /update_image route
     * @param image file which will be uploaded
     * @return [UploadFileResponse] on success
     * */
    @Multipart
    @POST("upload_image")
    suspend fun uploadImage(@Part image: MultipartBody.Part): UploadFileResponse

    /**
     * call's to retrieve all sections
     * sends [POST] request to /sections route
     * @return [SectionsResponse] on success
     * */
    @POST("sections")
    suspend fun retrieveSections(): SectionsResponse

    /**
     * call's to retrieve users active sections
     * sends [POST] request to `/active_sections` route
     * @return [ActiveSectionsResponse] on success
     * */
    @POST("active_sections")
    suspend fun retrieveActiveSections(): ActiveSectionsResponse

    /**
     * call's to retrieve categories by given [sectionId]
     * sends [POST] request to /categories/{section_id} route
     * @param sectionId id of section which categories should be retrieved
     * @return [CategoriesResponse] on success
     * */
    @POST("category/{section_id}")
    suspend fun retrieveCategories(
        @Path("section_id", encoded = true) sectionId: String,
    ): CategoriesResponse

    /**
     * call's to retrieve sub categories by given [subCategory]'s category id
     * sends [POST] request to /sub-categories/get route
     * @param subCategory id of category which sub categories should be retrieved
     * @return [CategoriesResponse] on success
     * */
    @POST("sub-categories/get")
    suspend fun retrieveSubCategories(
        @Body subCategory: SubCategoriesRequest,
    ): SubCategoriesResponse

    /**
     * call's to retrieve introduction videos
     * sends [POST] request to /introduction route
     * @return [IntroductionsResponse] on success
     * */
    @POST("introduction")
    suspend fun retrieveIntroductions(): IntroductionsResponse

    /**
     * call's to retrieve most listened categories
     * sends [POST] request to /category/most-listened route
     * @return [CategoriesResponse] on success
     * */
    @POST("category/most-listened")
    suspend fun retrieveMostListenerCategories(): CategoriesResponse

    /**
     * call's to retrieve prices
     * sends [POST] request to `/prices` route
     * @return [PricesResponse] on success
     * */
    @POST("prices")
    suspend fun retrievePrices(): PricesResponse

    /**
     * call's to buy section buy given [buy] params
     * sends [POST] request to /buy route
     * @param buy params to buy section
     * @return [Unit] on success
     * */
    @POST("buy")
    suspend fun buy(
        @Body buy: BuyRequest,
    )

    /**
     * call's to get total amount of courses in differrence currencies
     * sends [POST] request to /total-amount-get
     * @param buy param to get price of courses
     * @return [BuyTotalResponse] on success
     * */
    @POST("total-amount-get")
    suspend fun fetchTotalAmount(
        @Body buy: BuyRequest,
    ): BuyTotalResponse

    /**
     * call's to create buy receipt before buying
     * sends [POST] request to /payme-check/create route
     * @param buy params to create receipt
     * @return [BuyWithPaymeResponse] on success
     * */
    @POST("payme-check/create")
    suspend fun buyWithPayme(
        @Body buy: BuyRequest,
    ): BuyWithPaymeResponse

    /**
     * call's to create buy receipt before buying
     * sends [POST] request to /click-check/create route
     * @param buy params to create receipt
     * @return [BuyWithClickResponse] on success
     * */
    @POST("click-check/create")
    suspend fun buyWithClick(
        @Body buy: BuyRequest
    ):BuyWithClickResponse

    /**
     * call's tp create buy with robo cassa receipt before buying
     * sends [POST] request /buy-with/robokassa route
     * @param buy params to create robo cassa receipt
     * @return [BuyWithRoboKassaResponse] on success
     * */
    @POST("buy-with/robokassa")
    suspend fun buyWithRoboCassa(
        @Body buy: BuyRequest,
    ): BuyWithRoboKassaResponse

    /**
     * call's retrieve background musics by given [categoryId]
     * sends [POST] request to /background_music/{category_id} route
     * @param `categoryId` id of a category which background musics should be retrieved
     * @return [BackgroundMusicResponse] on success
     * */
    @POST("background_music/{category_id}")
    suspend fun retrieveBackgroundMusics(
        @Path("category_id", encoded = true) categoryId: String,
    ): BackgroundMusicResponse

    /**
     * call's retrieve all files by given [categoryId]
     * sends [POST] request to `/files/{file_id}` route
     * @param categoryId id of a category which files will be retrieved
     * @return [CategoryFilesResponse] on success
     * */
    @POST("files/{category_id}")
    suspend fun retrieveCategoryFiles(
        @Path("category_id", encoded = true) categoryId: String,
    ): CategoryFilesResponse

    /**
     * call's retrieve file by given [fileId]
     * sends [POST] request to `file/{file_id}` route
     * @param fileId id of a file which data will be retrieved
     * @return [FileResponse] on success
     * */
    @POST("file/{file_id}")
    suspend fun retrieveFile(
        @Path("file_id", encoded = true) fileId: String,
    ): FileResponse

    /**
     * Downloads any file from server with given [fileUrl]
     * @param fileUrl url of file after base url
     * @return [ResponseBody] response body inside input stream of file
     * */
    @GET("{file}")
    @Streaming
    suspend fun streamFile(
        @Path("file", encoded = true) fileUrl: String,
    ): ResponseBody

    /**
     * Sends the qr code to server
     * Sends the [POST] request to /qr-code/use route
     * @param qr code qr retrieved from image
     * */
    @POST("qr-code/use")
    suspend fun qr(@Body qr: QRRequest)

    /**
     * Retrieves notifications
     * Sends [POST] request to `/notifications-get` route
     * @return [NotificationsResponse] on success
     * */
    @POST("notifications-get")
    suspend fun retrieveNotifications(): NotificationsResponse
}