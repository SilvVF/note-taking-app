package io.silv.jikannoto.data.mocks

// class FakeCheckListLocalDataSource : CheckListLocalDataSource {
//
//    private val db = mutableMapOf<String, CheckListEntity>()
//
//    override suspend fun getAllItems(): Flow<List<CheckListEntity>> = flow {
//        emit(db.values.toList())
//    }
//
//    override suspend fun getItemById(id: String): CheckListEntity? {
//        return db[id]
//    }
//
//    override suspend fun deleteItemById(id: String): NotoApiResult<Nothing> {
//        val item = db.remove(id)
//        return if (item != null)
//            NotoApiResult.Success()
//        else NotoApiResult.Exception(null)
//    }
//
//    override suspend fun addLocallyDeleted(id: String) = Unit
//    override suspend fun removeLocallyDeleted(id: String) = Unit
//    override suspend fun upsertItem(name: String, completed: Boolean): NotoApiResult<Nothing> {
//        db[UUID.randomUUID().toString()] = CheckListEntity(
//            id = UUID.randomUUID().toString(),
//            System.currentTimeMillis(),
//            name,
//            completed
//        )
//        return NotoApiResult.Success()
//    }
// }