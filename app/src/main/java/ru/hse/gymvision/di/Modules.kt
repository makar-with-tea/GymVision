package ru.hse.gymvision.di

import android.media.tv.TvContract.Channels.Logo
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.hse.gymvision.domain.usecase.camera.AddCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.CheckCameraAccessibilityUseCase
import ru.hse.gymvision.domain.usecase.camera.DeleteCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.MoveCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.PlayVideoUseCase
import ru.hse.gymvision.domain.usecase.camera.RotateCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.SetMainCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.ZoomCameraUseCase
import ru.hse.gymvision.domain.usecase.db.GetGymListUseCase
import ru.hse.gymvision.domain.usecase.db.GetGymSchemeUseCase
import ru.hse.gymvision.domain.usecase.gym.ChooseGymUseCase
import ru.hse.gymvision.domain.usecase.gym.GetTrainerInfoUseCase
import ru.hse.gymvision.domain.usecase.user.ChangePasswordUseCase
import ru.hse.gymvision.domain.usecase.user.DeleteUserUseCase
import ru.hse.gymvision.domain.usecase.user.GetPastLoginUseCase
import ru.hse.gymvision.domain.usecase.user.GetUserInfoUseCase
import ru.hse.gymvision.domain.usecase.user.LoginUseCase
import ru.hse.gymvision.domain.usecase.user.LogoutUseCase
import ru.hse.gymvision.domain.usecase.user.RegisterUseCase
import ru.hse.gymvision.domain.usecase.user.UpdateUserUseCase
import ru.hse.gymvision.ui.account.AccountViewModel
import ru.hse.gymvision.ui.authorization.AuthorizationViewModel
import ru.hse.gymvision.ui.gymlist.GymListViewModel
import ru.hse.gymvision.ui.gymscheme.GymSchemeViewModel
import ru.hse.gymvision.ui.registration.RegistrationViewModel

val appModule = module {
    viewModel<AuthorizationViewModel> { AuthorizationViewModel(get(), get()) }
    viewModel<RegistrationViewModel> { RegistrationViewModel(get()) }
    viewModel<GymSchemeViewModel> { GymSchemeViewModel(get(), get()) }
    viewModel<GymListViewModel> { GymListViewModel(get()) }
    viewModel<AccountViewModel> { AccountViewModel(get(), get(), get(), get(), get()) }
}

val dataModule = module {

}

val domainModule = module {
    factory<ChangePasswordUseCase> { ChangePasswordUseCase() }
    factory<DeleteUserUseCase> { DeleteUserUseCase() }
    factory<GetPastLoginUseCase> { GetPastLoginUseCase() }
    factory<GetUserInfoUseCase> { GetUserInfoUseCase() }
    factory<LoginUseCase> { LoginUseCase() }
    factory<RegisterUseCase> { RegisterUseCase() }
    factory<UpdateUserUseCase> { UpdateUserUseCase() }
    factory<LogoutUseCase> { LogoutUseCase() }

    factory<ChooseGymUseCase> { ChooseGymUseCase() }
    factory<GetTrainerInfoUseCase> { GetTrainerInfoUseCase() }

    factory<GetGymListUseCase> { GetGymListUseCase() }
    factory<GetGymSchemeUseCase> { GetGymSchemeUseCase() }

    factory<AddCameraUseCase> { AddCameraUseCase() }
    factory<CheckCameraAccessibilityUseCase> { CheckCameraAccessibilityUseCase() }
    factory<DeleteCameraUseCase> { DeleteCameraUseCase() }
    factory<MoveCameraUseCase> { MoveCameraUseCase() }
    factory<PlayVideoUseCase> { PlayVideoUseCase() }
    factory<RotateCameraUseCase> { RotateCameraUseCase() }
    factory<SetMainCameraUseCase> { SetMainCameraUseCase() }
    factory<ZoomCameraUseCase> { ZoomCameraUseCase() }
}

//val dataModule = module {
//    single<LinesRepository> { LinesRepositoryWebImpl(get()) }
//    single<UserRepository> { UserRepositoryWebImpl(get()) }
//    single<PostRepository> { PostRepositoryImpl()}
//    single<SharedPrefRepository> { SharedPrefRepositoryImpl(context = androidContext()) }
//    single<HelloRepository> { HelloRepositoryImpl(get()) }
//    single<OkHttpClient> { OkHttpClient() }
//}
//
//val domainModule = module {
//    factory<LoadLinesUseCase> { LoadLinesUseCase(get()) }
//    factory<LoginUseCase> { LoginUseCase(get(), get()) }
//    factory<SignupUseCase> { SignupUseCase(get()) }
//    factory<GetPostsUseCase> { GetPostsUseCase(get()) }
//    factory<GetPastLoginUseCase> { GetPastLoginUseCase(get()) }
//    factory<LogoutUseCase> { LogoutUseCase(get()) }
//    factory<GetHelloUseCase> { GetHelloUseCase(get()) }
//    factory<GetPostsUseCase> { GetPostsUseCase(get()) }
//    factory<UploadPostUseCase> { UploadPostUseCase(get()) }
//}
//
//val appModule = module {
//    viewModel<MetroViewModel> { MetroViewModel(get(), get()) }
//    viewModel<LoginViewModel> { LoginViewModel(get(), get(), get()) }
//    viewModel<SignupViewModel> { SignupViewModel(get()) }
//    viewModel<StationViewModel> { StationViewModel(get()) }
//    viewModel<PostViewModel> { PostViewModel(get(), get()) }
//}