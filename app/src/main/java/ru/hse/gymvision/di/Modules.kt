package ru.hse.gymvision.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.hse.gymvision.ui.authorization.AuthorizationViewModel
import ru.hse.gymvision.ui.registration.RegistrationViewModel

val appModule = module {
    viewModel<AuthorizationViewModel> { AuthorizationViewModel() }
    viewModel<RegistrationViewModel> { RegistrationViewModel() }
}

val dataModule = module {

}

val domainModule = module {

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