package com.example.totalrecall.data

import androidx.room.Delete
import androidx.room.Update

class ResourceRepository(private val resourceDao: ResourceDAO) {
    //Resource Operations
    fun addResource(resource: Resource): Long =
        resourceDao.insertResource(resource)

    fun getAllResources() =
        resourceDao.getAllResources()

    fun getResource(id: Int) =
        resourceDao.getResource(id)

    fun deleteResource(res: Resource) =
        resourceDao.deleteResource(res)

    fun updateResource(resource: Resource) =
        resourceDao.updateResource(resource)

    fun getResourcesByTags(tags: IntArray) =
        resourceDao.getResourcesByTags(tags.size, tags)

    fun getResourcesById(ids: IntArray) =
        resourceDao.getResourcesById(ids)

    //Tag Operations
    fun getAllTags() =
        resourceDao.getAllTags()

    fun getAllTagNames() =
        resourceDao.getAllTagNames()

    fun addTag(tag: Tag) =
        resourceDao.addTag(tag)

    fun getTagByName(name: String) =
        resourceDao.getTagByName(name)

    fun getTagsFromResourceId(i: Int) =
        resourceDao.getTagsFromResourceId(i)

    fun clearUnboundTags(): Int =
        resourceDao.clearUnboundTags()

    //Resource Tag Join
    fun addResourceTagRel(resourceTagRel: ResourceTagRel) =
        resourceDao.addResourceTagRel(resourceTagRel)

    fun removeResourceTagRelation(rId: Int, tId: Int) =
        resourceDao.removeResourceTagRel(rId, tId)

    //Contributors
    fun addContributor(contributor: Contributor) =
        resourceDao.addContributor(contributor)


    fun getContributorCountByResource(resourceId: Int): Int =
        resourceDao.getContributorCountByResource(resourceId)

    fun getContributorsForResource(resourceId: Int): List<Contributor> =
        resourceDao.getContributorsForResource(resourceId)

    fun updateContributor(contributor: Contributor) =
        resourceDao.updateContributor(contributor)

    fun deleteContributor(resourceId: Int, position: Int) =
        resourceDao.deleteContributor(resourceId, position)
}