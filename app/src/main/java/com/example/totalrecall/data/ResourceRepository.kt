package com.example.totalrecall.data

class ResourceRepository(private val resourceDao: ResourceDAO) {
    //Resource Operations
    suspend fun addResource(resource: Resource) =
        resourceDao.insertResource(resource)

    suspend fun getAllResources() =
        resourceDao.getAllResources()

    suspend fun getResource(id: Int) =
        resourceDao.getResource(id)

    suspend fun deleteResource(res: Resource) =
        resourceDao.deleteResource(res)

    suspend fun updateResource(resource: Resource) =
        resourceDao.updateResource(resource)

    suspend fun getResourcesByTags(tags: IntArray) =
        resourceDao.getResourcesByTags(tags.size, tags)

    suspend fun getResourcesById(ids: IntArray) =
        resourceDao.getResourcesById(ids)

    //Tag Operations
    suspend fun getAllTags() =
        resourceDao.getAllTags()

    suspend fun getAllTagNames() =
        resourceDao.getAllTagNames()

    suspend fun addTag(tag: Tag) =
        resourceDao.addTag(tag)

    suspend fun getTagByName(name: String) =
        resourceDao.getTagByName(name)

    suspend fun getTagsFromResourceId(i: Int) =
        resourceDao.getTagsFromResourceId(i)

    suspend fun clearUnboundTags(): Int =
        resourceDao.clearUnboundTags()

    //Resource Tag Join
    suspend fun addResourceTagRel(resourceTagRel: ResourceTagRel) =
        resourceDao.addResourceTagRel(resourceTagRel)

    suspend fun removeResourceTagRelation(rId: Int, tId: Int) =
        resourceDao.removeResourceTagRel(rId, tId)
}