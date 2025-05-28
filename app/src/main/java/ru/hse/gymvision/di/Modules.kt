package ru.hse.gymvision.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.hse.gymvision.data.AuthInterceptor
import ru.hse.gymvision.data.GlobalRepositoryImpl
import ru.hse.gymvision.data.SharedPrefRepositoryImpl
import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository
import ru.hse.gymvision.domain.usecase.camera.ChangeAiStateUseCase
import ru.hse.gymvision.domain.usecase.camera.CheckCameraAccessibilityUseCase
import ru.hse.gymvision.domain.usecase.camera.GetCameraIdsUseCase
import ru.hse.gymvision.domain.usecase.camera.GetCameraLinksUseCase
import ru.hse.gymvision.domain.usecase.camera.GetNewCameraLinkUseCase
import ru.hse.gymvision.domain.usecase.camera.MoveCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.RotateCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.SaveCamerasUseCase
import ru.hse.gymvision.domain.usecase.camera.ZoomCameraUseCase
import ru.hse.gymvision.domain.usecase.gym.GetGymIdUseCase
import ru.hse.gymvision.domain.usecase.gym.GetGymListUseCase
import ru.hse.gymvision.domain.usecase.gym.GetGymSchemeUseCase
import ru.hse.gymvision.domain.usecase.gym.SaveGymIdUseCase
import ru.hse.gymvision.domain.usecase.user.ChangePasswordUseCase
import ru.hse.gymvision.domain.usecase.user.CheckPasswordUseCase
import ru.hse.gymvision.domain.usecase.user.DeleteUserUseCase
import ru.hse.gymvision.domain.usecase.user.GetPastLoginUseCase
import ru.hse.gymvision.domain.usecase.user.GetUserInfoUseCase
import ru.hse.gymvision.domain.usecase.user.LoginUseCase
import ru.hse.gymvision.domain.usecase.user.LogoutUseCase
import ru.hse.gymvision.domain.usecase.user.RegisterUseCase
import ru.hse.gymvision.domain.usecase.user.UpdateUserUseCase
import ru.hse.gymvision.ui.account.AccountViewModel
import ru.hse.gymvision.ui.authorization.AuthorizationViewModel
import ru.hse.gymvision.ui.camera.CameraViewModel
import ru.hse.gymvision.ui.gymlist.GymListViewModel
import ru.hse.gymvision.ui.gymscheme.GymSchemeViewModel
import ru.hse.gymvision.ui.registration.RegistrationViewModel


val appModule = module {
    viewModel<AuthorizationViewModel> { AuthorizationViewModel(get(), get()) }
    viewModel<RegistrationViewModel> { RegistrationViewModel(get()) }
    viewModel<GymSchemeViewModel> { GymSchemeViewModel(get(), get(), get(), get()) }
    viewModel<GymListViewModel> { GymListViewModel(get()) }
    viewModel<AccountViewModel> { AccountViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel<CameraViewModel> { CameraViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}

@RequiresApi(Build.VERSION_CODES.O)
val dataModule = module {
    single<SharedPrefRepository> { SharedPrefRepositoryImpl(androidContext()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(get<SharedPrefRepository>()))
            .build()
    }

    single {
        val gson = GsonBuilder().create()

        Retrofit.Builder()
            .baseUrl("http://192.168.0.106:8000/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    single<GlobalApiService> { get<Retrofit>().create(GlobalApiService::class.java) }

    single<GlobalRepository> { GlobalRepositoryImpl(get()) }
}

val domainModule = module {
    factory<ChangePasswordUseCase> { ChangePasswordUseCase(get()) }
    factory<DeleteUserUseCase> { DeleteUserUseCase(get(), get()) }
    factory<GetPastLoginUseCase> { GetPastLoginUseCase(get()) }
    factory<GetUserInfoUseCase> { GetUserInfoUseCase(get(), get()) }
    factory<LoginUseCase> { LoginUseCase(get(), get()) }
    factory<RegisterUseCase> { RegisterUseCase(get(), get()) }
    factory<UpdateUserUseCase> { UpdateUserUseCase(get()) }
    factory<LogoutUseCase> { LogoutUseCase(get()) }

    factory<GetGymListUseCase> { GetGymListUseCase(get()) }
    factory<GetGymSchemeUseCase> { GetGymSchemeUseCase(get()) }
    factory<GetGymIdUseCase> { GetGymIdUseCase(get()) }
    factory<SaveGymIdUseCase> { SaveGymIdUseCase(get()) }

    factory<CheckCameraAccessibilityUseCase> { CheckCameraAccessibilityUseCase(get()) }
    factory<MoveCameraUseCase> { MoveCameraUseCase(get()) }
    factory<RotateCameraUseCase> { RotateCameraUseCase(get()) }
    factory<ZoomCameraUseCase> { ZoomCameraUseCase(get()) }
    factory<SaveCamerasUseCase> { SaveCamerasUseCase(get()) }
    factory<GetCameraIdsUseCase> { GetCameraIdsUseCase(get()) }
    factory<GetNewCameraLinkUseCase> { GetNewCameraLinkUseCase(get()) }
    factory<GetCameraLinksUseCase> { GetCameraLinksUseCase(get()) }
    factory<ChangeAiStateUseCase> { ChangeAiStateUseCase(get()) }
    factory<CheckPasswordUseCase> { CheckPasswordUseCase(get()) }
}
