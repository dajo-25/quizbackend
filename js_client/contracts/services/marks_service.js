import { DTOResponse, DTOParams, EmptyParamsDTO, SearchQuestionsParamsDTO, GetQuestionParamsDTO, GetQuestionsBatchParamsDTO, UpdateQuestionParamsDTO, DeleteQuestionParamsDTO, UpdateCollectionParamsDTO, EmptyRequestDTO, BaseResponse, LoginResponseDTO, GenericResponseDTO, MessageResponseDTO, MustChangePasswordResponseDTO, UserStatusResponseDTO, ProfileDataResponseDTO, QuestionListResponseDTO, QuestionDataResponseDTO, CollectionListResponseDTO, IdDataResponseDTO, CollectionDetailResponseDTO, MarkListResponseDTO, FriendRequestResponseDTO, UserListResponseDTO, LoginRequestDTO, LoginDataDTO, SignupRequestDTO, MessageDataDTO, RecoverPasswordRequestDTO, VerifyEmailRequestDTO, MustChangePasswordDataDTO, ChangePasswordRequestDTO, UserStatusDataDTO, RegisterPushTokenRequestDTO, ProfileDataDTO, UpdateProfileRequestDTO, AnswerDataDTO, QuestionDataDTO, LocalizationDTO, CreateAnswerInputDTO, CreateQuestionInputDTO, CreateQuestionsRequestDTO, UpdateAnswerInputDTO, UpdateQuestionRequestDTO, CollectionDataDTO, CollectionDetailDataDTO, CreateCollectionRequestDTO, IdDataDTO, UpdateCollectionRequestDTO, MarkDataDTO, SendFriendRequestDTO, FriendRequestDataDTO, RespondFriendRequestRequestDTO, FriendRequestListResponseDTO, PublicUserProfileDTO, ErrorDetailsDTO } from '../dtos.js';
import * as Enums from '../enums.js';

export class MarksService {
  constructor(baseUrl, fetcher) {
    this.baseUrl = baseUrl;
    this.fetcher = fetcher;
  }

  async getMarks({ bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/marks`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => MarkListResponseDTO.fromJson(data));
  }

}
