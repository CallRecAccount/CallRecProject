package uz.invan.rovitalk.ui.profile.edit

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.user.Profile
import uz.invan.rovitalk.data.models.validation.exceptions.LengthCredentialData
import uz.invan.rovitalk.databinding.ScreenEditProfileBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.util.ktx.phone
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.lifecycle.EventObserver
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class EditProfileScreen :
    BaseScreen<ScreenEditProfileBinding>(true, direction = MainDirections.PROFILE) {
    private val viewModel by viewModels<EditProfileViewModel>()
    private var maskedListener: MaskedTextChangedListener? = null
    private var image: File? = null

    private var isInitialized = AtomicBoolean(false)

    override fun setBinding() = ScreenEditProfileBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.initializeEditable.observe(viewLifecycleOwner, initializeEditableObserver)
        viewModel.navigateSettings.observe(viewLifecycleOwner, navigateSettingsObserver)
        viewModel.profileUpdated.observe(viewLifecycleOwner, profileUpdatedObserver)
        viewModel.incorrectFirstName.observe(viewLifecycleOwner, incorrectFirstNameObserver)
        viewModel.incorrectLastName.observe(viewLifecycleOwner, incorrectLastNameObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.userNotFound.observe(viewLifecycleOwner, userNotFoundObserver)
        viewModel.userAlreadyExists.observe(viewLifecycleOwner, userAlreadyExistsObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        binding.back.setOnClickListener { exit() }
        binding.settings.setOnClickListener { viewModel.navigateSettings() }
        binding.imagePerson.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(300, 400)
                .start()
        }
        binding.updateProfileData.setOnClickListener {
            viewModel.editProfile(
                firstName = binding.editableFirstName.text.toString(),
                lastName = binding.editableLastName.text.toString(),
                photo = image
            )
            isInitialized.set(true)
        }
    }

    private val initializeEditableObserver: Observer<String> = Observer {
/*        maskedListener = MaskedTextChangedListener(
            format = format,
            field = binding.phoneNumber,
            valueListener = object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String,
                ) {
                    viewModel.onPhoneChanged(extractedValue)
                }
            }
        )
        binding.phoneNumber.addTextChangedListener(maskedListener)
        binding.phoneNumber.onFocusChangeListener = maskedListener*/
    }

    private val navigateSettingsObserver: EventObserver<Unit> = EventObserver {
        controller.navigate(EditProfileScreenDirections.navigateSettings())
    }

    private val profileUpdatedObserver: EventObserver<Profile> = EventObserver { profile ->
        // exits if data updated
        if (isInitialized.get()) {
            roviSuccess(getString(R.string.updated))
            exit()
        }

        // set image if exists
        profile.photo?.run {
            Glide.with(this@EditProfileScreen)
                .load(this)
                .circleCrop()
                .into(binding.imagePerson)
        }

        binding.phoneNumber.setText(profile.phone.phone())
        binding.editableFirstName.clearFocus()
        binding.editableLastName.clearFocus()
        maskedListener?.setText(profile.phone)
        binding.editableFirstName.setText(profile.firstName)
        binding.editableLastName.setText(profile.lastName)
    }

    private val incorrectFirstNameObserver: EventObserver<LengthCredentialData> =
        EventObserver { firstName ->
            roviError(getString(R.string.exception_wrong_first_name_length, firstName.length))
        }

    private val incorrectLastNameObserver: EventObserver<LengthCredentialData> =
        EventObserver { lastName ->
            roviError(getString(R.string.exception_wrong_last_name_length, lastName.length))
        }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_wrong_validation))
    }

    private val userNotFoundObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_user_not_found))
        controller.navigate(RoviNavigationDirections.splash())
    }

    private val userAlreadyExistsObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_user_already_exists))
    }

    private val unauthorizedObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unauthorized))
        controller.navigate(RoviNavigationDirections.splash())
    }

    private val unknownErrorObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unknown_exception))
    }

    private val noInternetObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_no_internet))
    }

    private val timeOutObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_time_out))
    }

    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            image = ImagePicker.getFile(data)

            data?.data?.run {
                Glide.with(this@EditProfileScreen)
                    .load(this)
                    .circleCrop()
                    .into(binding.imagePerson)
            }
        }
    }
}