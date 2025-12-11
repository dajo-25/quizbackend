package com.quizbackend.contracts.generated

import kotlinx.serialization.Serializable

@Serializable
enum class ErrorType {
    INVALID_CREDENTIALS,
    ACCOUNT_ALREADY_EXISTS,
    EMAIL_NOT_VERIFIED,
    INVALID_VERIFICATION_CODE,
    PASSWORD_TOO_SHORT,
    PASSWORD_REQUIRES_NUMBER,
    PASSWORD_REQUIRES_UPPERCASE,
    PASSWORD_RECENTLY_USED,
    INVALID_EMAIL_FORMAT,
    TOKEN_EXPIRED,
    INVALID_TOKEN,
    UNAUTHORIZED,
    FORBIDDEN,
    USER_NOT_FOUND,
    USERNAME_ALREADY_TAKEN,
    INVALID_PROFILE_DATA,
    QUESTION_NOT_FOUND,
    INVALID_QUESTION_DATA,
    EMPTY_QUESTION_TEXT,
    MISSING_CORRECT_ANSWER,
    TOO_MANY_ANSWERS,
    COLLECTION_NOT_FOUND,
    COLLECTION_ALREADY_EXISTS,
    CANNOT_MODIFY_PUBLIC_COLLECTION,
    ACCESS_DENIED_TO_COLLECTION,
    FRIEND_REQUEST_ALREADY_SENT,
    FRIEND_REQUEST_NOT_FOUND,
    CANNOT_FRIEND_SELF,
    USER_BLOCKED,
    INTERNAL_SERVER_ERROR,
    BAD_REQUEST,
    METHOD_NOT_ALLOWED,
    NOT_IMPLEMENTED,
    ROUTE_NOT_EXISTS_OR_BAD_IMPLEMENTED,
    MISSING_DATA
}

@Serializable
abstract class DTOParams

@Serializable
class EmptyParamsDTO : DTOParams()

@Serializable
data class SearchQuestionsParamsDTO(
    val page: Int,
    val locale: String? = null
) : DTOParams()

@Serializable
data class GetQuestionParamsDTO(
    val id: Int,
    val locale: String? = null
) : DTOParams()

@Serializable
data class GetQuestionsBatchParamsDTO(
    val ids: List<Int>,
    val locale: String? = null
) : DTOParams()

@Serializable
data class UpdateQuestionParamsDTO(
    val id: Int
) : DTOParams()

@Serializable
data class DeleteQuestionParamsDTO(
    val id: Int
) : DTOParams()

@Serializable
data class UpdateCollectionParamsDTO(
    val id: Int
) : DTOParams()

@Serializable
data class DTOResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: ErrorDetailsDTO? = null
)

@Serializable
class EmptyRequestDTO

@Serializable
data class BaseResponse(
    val success: Boolean,
    val error: String
)

@Serializable
data class LoginResponseDTO(
    val token: String
)

@Serializable
data class GenericResponseDTO(
    val success: Boolean
)

@Serializable
data class MessageResponseDTO(
    val message: String
)

@Serializable
data class MustChangePasswordResponseDTO(
    val mustChange: Boolean
)

@Serializable
data class UserStatusResponseDTO(
    val status: UserStatusDataDTO? = null
)

@Serializable
data class ProfileDataResponseDTO(
    val profile: ProfileDataDTO
)

@Serializable
data class QuestionListResponseDTO(
    val questions: List<QuestionDataDTO>
)

@Serializable
data class QuestionDataResponseDTO(
    val question: QuestionDataDTO
)

@Serializable
data class CollectionListResponseDTO(
    val collections: List<CollectionDataDTO>
)

@Serializable
data class IdDataResponseDTO(
    val id: Int
)

@Serializable
data class CollectionDetailResponseDTO(
    val collection: CollectionDetailDataDTO? = null
)

@Serializable
data class MarkListResponseDTO(
    val marks: List<MarkDataDTO>
)

@Serializable
data class FriendRequestResponseDTO(
    val friendRequest: FriendRequestDataDTO? = null
)

@Serializable
data class UserListResponseDTO(
    val users: List<PublicUserProfileDTO>
)

@Serializable
data class LoginRequestDTO(
    val email: String,
    val passwordHash: String,
    val uniqueId: String
)

@Serializable
data class LoginDataDTO(
    val token: String,
    val message: String
)

@Serializable
data class SignupRequestDTO(
    val email: String,
    val username: String,
    val name: String,
    val surname: String,
    val passwordHash: String
)

@Serializable
data class MessageDataDTO(
    val message: String
)

@Serializable
data class RecoverPasswordRequestDTO(
    val email: String
)

@Serializable
data class VerifyEmailRequestDTO(
    val code: String
)

@Serializable
data class MustChangePasswordDataDTO(
    val must_change_password: Boolean
)

@Serializable
data class ChangePasswordRequestDTO(
    val oldHash: String,
    val newHash: String
)

@Serializable
data class UserStatusDataDTO(
    val id: Int? = null,
    val email: String? = null,
    val username: String? = null,
    val isVerified: Boolean,
    val mustChangePassword: Boolean
)

@Serializable
data class RegisterPushTokenRequestDTO(
    val pushToken: String
)

@Serializable
data class ProfileDataDTO(
    val id: Int,
    val email: String,
    val username: String,
    val name: String,
    val surname: String,
    val isVerified: Boolean? = null
)

@Serializable
data class UpdateProfileRequestDTO(
    val name: String,
    val surname: String,
    val username: String
)

@Serializable
data class AnswerDataDTO(
    val id: Int,
    val text: String,
    val isCorrect: Boolean
)

@Serializable
data class QuestionDataDTO(
    val id: Int,
    val text: String,
    val answers: List<AnswerDataDTO>
)

@Serializable
data class LocalizationDTO(
    val locale: String,
    val text: String
)

@Serializable
data class CreateAnswerInputDTO(
    val localizations: List<LocalizationDTO>
)

@Serializable
data class CreateQuestionInputDTO(
    val localizations: List<LocalizationDTO>,
    val answers: List<CreateAnswerInputDTO>,
    val correctAnswersIndices: List<Int>,
    val isDiscoverable: Boolean,
    val collectionIds: List<Int>
)

@Serializable
data class CreateQuestionsRequestDTO(
    val questions: List<CreateQuestionInputDTO>
)

@Serializable
data class UpdateAnswerInputDTO(
    val id: Int,
    val localizations: List<LocalizationDTO>
)

@Serializable
data class UpdateQuestionRequestDTO(
    val localizations: List<LocalizationDTO>,
    val answers: List<UpdateAnswerInputDTO>,
    val correctAnswersIndices: List<Int>,
    val isDiscoverable: Boolean,
    val collectionIds: List<Int>
)

@Serializable
data class CollectionDataDTO(
    val id: Int,
    val name: String,
    val description: String,
    val isPublic: Boolean,
    val creatorId: Int,
    val createdAt: String
)

@Serializable
data class CollectionDetailDataDTO(
    val id: Int,
    val name: String,
    val description: String,
    val isPublic: Boolean,
    val creatorId: Int,
    val createdAt: String,
    val questionIds: List<Int>
)

@Serializable
data class CreateCollectionRequestDTO(
    val name: String,
    val description: String,
    val isPublic: Boolean
)

@Serializable
data class IdDataDTO(
    val id: Int
)

@Serializable
data class UpdateCollectionRequestDTO(
    val name: String,
    val description: String,
    val isPublic: Boolean
)

@Serializable
data class MarkDataDTO(
    val id: Int,
    val questionId: Int,
    val isCorrect: Boolean,
    val createdAt: String
)

@Serializable
data class SendFriendRequestDTO(
    val targetUserId: Int
)

@Serializable
data class FriendRequestDataDTO(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val status: String,
    val createdAt: String
)

@Serializable
data class PublicUserProfileDTO(
    val id: Int,
    val username: String,
    val name: String,
    val surname: String
)

@Serializable
data class ErrorDetailsDTO(
    val type: ErrorType,
    val message: String? = null
)

