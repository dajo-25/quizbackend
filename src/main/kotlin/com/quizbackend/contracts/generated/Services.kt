package com.quizbackend.contracts.generated

import com.quizbackend.contracts.generated.*

interface AuthService {
    suspend fun PostLogin(body: LoginRequestDTO, params: EmptyParamsDTO): DTOResponse<LoginResponseDTO>
    suspend fun PostSignup(body: SignupRequestDTO, params: EmptyParamsDTO): DTOResponse<LoginResponseDTO>
    suspend fun PostLogout(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun DeleteAccount(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<MessageResponseDTO>
    suspend fun PostRecover(body: RecoverPasswordRequestDTO, params: EmptyParamsDTO): DTOResponse<MessageResponseDTO>
    suspend fun PostVerify(body: VerifyEmailRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun GetMustChangePassword(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<MustChangePasswordResponseDTO>
    suspend fun PostChangePassword(body: ChangePasswordRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun GetStatus(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<UserStatusResponseDTO>
}

interface QuestionsService {
    suspend fun GetQuestions(body: EmptyRequestDTO, params: SearchQuestionsParamsDTO): DTOResponse<QuestionListResponseDTO>
    suspend fun PostQuestions(body: CreateQuestionsRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun GetQuestionsId(body: EmptyRequestDTO, params: GetQuestionParamsDTO): DTOResponse<QuestionDataResponseDTO>
    suspend fun PutQuestionsId(body: UpdateQuestionRequestDTO, params: UpdateQuestionParamsDTO): DTOResponse<QuestionDataResponseDTO>
    suspend fun DeleteQuestionsId(body: EmptyRequestDTO, params: DeleteQuestionParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun GetBatch(body: EmptyRequestDTO, params: GetQuestionsBatchParamsDTO): DTOResponse<QuestionListResponseDTO>
}

interface CollectionsService {
    suspend fun GetCollections(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<CollectionListResponseDTO>
    suspend fun PostCollections(body: CreateCollectionRequestDTO, params: EmptyParamsDTO): DTOResponse<IdDataResponseDTO>
    suspend fun GetCollectionsId(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<CollectionDetailResponseDTO>
    suspend fun PutCollectionsId(body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun DeleteCollectionsId(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO>
}

interface CommunitiesService {
    suspend fun PostFriendRequest(body: SendFriendRequestDTO, params: EmptyParamsDTO): DTOResponse<FriendRequestResponseDTO>
    suspend fun PostRespond(body: RespondFriendRequestRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun GetFriendRequests(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<FriendRequestListResponseDTO>
    suspend fun GetUsers(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<UserListResponseDTO>
}

interface MarksService {
    suspend fun GetMarks(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<MarkListResponseDTO>
}

interface DevicesService {
    suspend fun PostPushToken(body: RegisterPushTokenRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO>
    suspend fun DeletePushToken(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO>
}

interface ProfileService {
    suspend fun GetProfile(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<ProfileDataResponseDTO>
    suspend fun PutProfile(body: UpdateProfileRequestDTO, params: EmptyParamsDTO): DTOResponse<ProfileDataResponseDTO>
}

