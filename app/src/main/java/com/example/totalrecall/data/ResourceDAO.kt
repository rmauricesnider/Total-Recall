package com.example.totalrecall.data

import androidx.room.*

@Dao
interface ResourceDAO {
    //Resource
    @Insert(entity = Resource::class)
    fun insertResource(resource: Resource): Long

    @Query("SELECT * FROM resources")
    fun getAllResources(): List<Resource>

    @Query("SELECT * FROM resources WHERE resourceId = :id")
    fun getResource(id: Int): Resource

    @Query("SELECT * FROM resources WHERE resourceId IN (:ids)")
    fun getResourcesById(ids: IntArray): List<Resource>

    @Query("SELECT r.* FROM tags t, resource_tag_join rt, resources r " +
            "WHERE rt.tagId = t.tagId " +
            "AND (t.tagId IN (:tags)) " +
            "AND r.resourceId = rt.resourceId " +
            "GROUP BY r.resourceId " +
            "HAVING COUNT (r.resourceId) = :length")
    fun getResourcesByTags(length: Int, tags: IntArray): List<Resource>

    @Delete
    fun deleteResource(res: Resource)

    @Query("DELETE FROM resources WHERE resourceId = :id")
    fun deleteResource(id: Int)

    @Update
    fun updateResource(res: Resource)

    //Tag
    @Insert(entity = Tag::class)
    fun addTag(tag: Tag)

    @Query("SELECT * FROM tags")
    fun getAllTags(): List<Tag>

    @Query("SELECT name FROM tags")
    fun getAllTagNames(): List<String>

    @Query("SELECT * FROM tags WHERE name = :name")
    fun getTagByName(name: String): Tag?

    @Query("DELETE FROM tags WHERE tagId IN " +
            "(SELECT tags.tagId FROM tags LEFT JOIN resource_tag_join ON tags.tagId = resource_tag_join.tagId WHERE resource_tag_join.resourceId IS NULL)")
    fun clearUnboundTags(): Int

    //Rel
    @Insert(entity = ResourceTagRel::class)
    fun addResourceTagRel(resourceTagRel: ResourceTagRel)

    @Query("DELETE FROM resource_tag_join WHERE resource_tag_join.resourceId = :rId AND resource_tag_join.tagId = :tId")
    fun removeResourceTagRel(rId: Int, tId: Int)

    @Query("SELECT t.* FROM tags t, resources r, resource_tag_join rt " +
            "WHERE rt.resourceId IN (:resourceId) " +
            "AND rt.tagId = t.tagId " +
            "GROUP BY t.tagId")
    fun getTagsFromResourceId(resourceId: Int): List<Tag>

    @Query("SELECT resourceId FROM resource_tag_join WHERE tagId = :resourceId")
    fun getResourceFromTags(resourceId: Int): List<Int>

    @Query("SELECT * FROM resource_tag_join")
    fun getAllResourceTagRel(): List<ResourceTagRel>

    //Contributor
    @Insert(entity = Contributor::class)
    fun addContributor(contributor: Contributor)

    @Query("SELECT COUNT(*) FROM contributors WHERE resourceId = :resourceId")
    fun getContributorCountByResource(resourceId: Int): Int

    @Query("SELECT * FROM contributors WHERE resourceId = :resourceId")
    fun getContributorsForResource(resourceId: Int): List<Contributor>

    @Update
    fun updateContributor(contributor: Contributor)

    @Query("DELETE FROM contributors WHERE resourceId = :resourceId AND position = :position")
    fun deleteContributor(resourceId: Int, position: Int)
}