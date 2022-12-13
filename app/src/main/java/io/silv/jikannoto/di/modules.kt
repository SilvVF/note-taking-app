package io.silv.jikannoto.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.silv.jikannoto.MainActivityViewModel
import io.silv.jikannoto.NotoDatabase
import io.silv.jikannoto.data.AppDataStoreRepository
import io.silv.jikannoto.data.NotoRepositoryImpl
import io.silv.jikannoto.data.local.NotoLocalDataSource
import io.silv.jikannoto.data.local.NotoLocalDataSourceImpl
import io.silv.jikannoto.data.remote.NotoRemoteDataSource
import io.silv.jikannoto.data.remote.NotoRemoteDataSourceImpl
import io.silv.jikannoto.data.remote.UserRepositoryImpl
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.domain.UserRepository
import io.silv.jikannoto.presentation.screens.noto_list.NotoListViewModel
import io.silv.jikannoto.presentation.screens.noto_view.NotoViewViewModel
import io.silv.jikannoto.presentation.screens.user_settings.UserSettingsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Firebase.firestore
    }
    single {
        FirebaseAuth.getInstance()
    }
    single {
        AppDataStoreRepository(androidContext(), get())
    }

    viewModel {
        MainActivityViewModel(
            get(), get()
        )
    }
    single<UserRepository> {
        UserRepositoryImpl(
            get(),
            get()
        )
    }
    viewModel {
        UserSettingsViewModel(
            get(),
            get()
        )
    }

    viewModel {
        NotoListViewModel(get())
    }
    single<NotoLocalDataSource> {
        NotoLocalDataSourceImpl(
            NotoDatabase(
                AndroidSqliteDriver(
                    NotoDatabase.Schema, androidContext(), "noto.db"
                )
            ),
            get()
        )
    }
    single<NotoRemoteDataSource> {
        NotoRemoteDataSourceImpl(
            get(), get()
        )
    }
    single {
        NotoRepositoryImpl(get(), get(), get(), get())
    }

    single<NotoDispatchers> {
        object : NotoDispatchers {
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
            override val default: CoroutineDispatcher
                get() = Dispatchers.Default
            override val unconfined: CoroutineDispatcher
                get() = Dispatchers.Unconfined
        }
    }
    viewModel {
        NotoViewViewModel(get())
    }
}

val authModule = module {
}