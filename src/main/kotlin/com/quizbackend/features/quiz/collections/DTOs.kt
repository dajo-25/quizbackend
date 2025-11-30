package com.quizbackend.features.quiz.collections

import kotlinx.serialization.Serializable

@Serializable
data class CreateCollectionRequest(
    val name: String,
    val description: String? = null,
    val isPublic: Boolean
)

@Serializable
data class UpdateCollectionRequest(
    val name: String? = null,
    val description: String? = null,
    val isPublic: Boolean? = null
)

@Serializable
data class ShareCollectionRequest(
    val userEmail: String
)

@Serializable
data class CollectionResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val creatorId: Int,
    val createdAt: String
)

@Serializable
data class CollectionDetailResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val creatorId: Int,
    val createdAt: String,
    val questionIds: List<Int>
)
