package io.silv.jikannoto.data.remote

import com.google.firebase.auth.FirebaseAuth
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.data.util.onNoExceptionThrown
import io.silv.jikannoto.data.util.safeCall
import io.silv.jikannoto.domain.models.User
import io.silv.jikannoto.domain.result.NotoApiResult
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
class UserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val dispatcher: NotoDispatchers
) : UserRepository {

    override suspend fun sendPasswordReset(email: String): NotoApiResult<Nothing> = withContext(dispatcher.io) {
        val res = runCatching {
            firebaseAuth.sendPasswordResetEmail(email)
        }.onSuccess {
            return@withContext NotoApiResult.Success<Nothing>()
        }
        NotoApiResult.Exception<Nothing>("failed to send")
    }
    override suspend fun getUserInfo(): User = User(firebaseAuth.currentUser?.email ?: "not signed in")
    override suspend fun login(
        username: String,
        password: String
    ): NotoApiResult<Nothing> = withContext(dispatcher.io) {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(username, password).await()
            if (result.user != null) {
                NotoApiResult.Success()
            } else NotoApiResult.Exception("Unable to Sign in")
        } catch (e: Exception) {
            NotoApiResult.Exception(e.message)
        }
    }

    override suspend fun logout(
        username: String,
        password: String
    ): NotoApiResult<Nothing> = withContext(dispatcher.io) {
        safeCall {
            firebaseAuth.signOut()
            onNoExceptionThrown { NotoApiResult.Success() }
        }
    }

    override suspend fun register(
        username: String,
        password: String
    ): NotoApiResult<Nothing> = withContext(dispatcher.io) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(username, password).await()
            val result = firebaseAuth.signInWithEmailAndPassword(username, password).await()
            if (result.user != null) {
                NotoApiResult.Success()
            } else NotoApiResult.Exception("Unable to Sigjn in")
        } catch (e: Exception) {
            NotoApiResult.Exception(e.message)
        }
    }
}
