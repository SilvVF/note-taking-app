package io.silv.jikannoto.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.silv.jikannoto.MainActivityViewModel
import io.silv.jikannoto.NotoDatabase
import io.silv.jikannoto.data.AppDataStoreRepository
import io.silv.jikannoto.data.CheckListRepositoryImpl
import io.silv.jikannoto.data.NotoRepositoryImpl
import io.silv.jikannoto.data.local.CheckListLocalDataSource
import io.silv.jikannoto.data.local.CheckListLocalDataSourceImpl
import io.silv.jikannoto.data.local.NotoLocalDataSource
import io.silv.jikannoto.data.local.NotoLocalDataSourceImpl
import io.silv.jikannoto.data.remote.*
import io.silv.jikannoto.data.util.Crypto
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.presentation.screens.check_list.CheckListViewModel
import io.silv.jikannoto.presentation.screens.noto_list.NotoListViewModel
import io.silv.jikannoto.presentation.screens.noto_view.NotoViewViewModel
import io.silv.jikannoto.presentation.screens.user_settings.UserSettingsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {

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

    factoryOf(::UserRepositoryImpl) bind UserRepository::class
    factoryOf(::NotoRemoteDataSourceImpl) bind NotoRemoteDataSource::class
    factoryOf(::NotoLocalDataSourceImpl) bind NotoLocalDataSource::class

    factoryOf(::CheckListRemoteDataSourceImpl) bind CheckListRemoteDataSource::class
    factoryOf(::CheckListLocalDataSourceImpl) bind CheckListLocalDataSource::class

    factoryOf(::CheckListRepositoryImpl)
    factoryOf(::NotoRepositoryImpl)
    factoryOf(::Crypto)

    viewModelOf(::UserSettingsViewModel)
    viewModelOf(::NotoListViewModel)
    viewModelOf(::MainActivityViewModel)
    viewModelOf(::NotoViewViewModel)
    viewModelOf(::CheckListViewModel)
}

val dataModule = module {

    single {
        AppDataStoreRepository(androidContext(), get())
    }

    single {
        Firebase.firestore
    }

    single {
        NotoDatabase(
            AndroidSqliteDriver(
                NotoDatabase.Schema, androidContext(), "noto.db"
            )
        )
    }

    single {
        FirebaseAuth.getInstance()
    }
}