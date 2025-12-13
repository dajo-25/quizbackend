import { DTOResponse, DTOParams, EmptyParamsDTO, SearchQuestionsParamsDTO, GetQuestionParamsDTO, GetQuestionsBatchParamsDTO, UpdateQuestionParamsDTO, DeleteQuestionParamsDTO, UpdateCollectionParamsDTO, EmptyRequestDTO, BaseResponse, LoginResponseDTO, GenericResponseDTO, MessageResponseDTO, MustChangePasswordResponseDTO, UserStatusResponseDTO, ProfileDataResponseDTO, QuestionListResponseDTO, QuestionDataResponseDTO, CollectionListResponseDTO, IdDataResponseDTO, CollectionDetailResponseDTO, MarkListResponseDTO, FriendRequestResponseDTO, UserListResponseDTO, LoginRequestDTO, LoginDataDTO, SignupRequestDTO, MessageDataDTO, RecoverPasswordRequestDTO, VerifyEmailRequestDTO, MustChangePasswordDataDTO, ChangePasswordRequestDTO, UserStatusDataDTO, RegisterPushTokenRequestDTO, ProfileDataDTO, UpdateProfileRequestDTO, AnswerDataDTO, QuestionDataDTO, LocalizationDTO, CreateAnswerInputDTO, CreateQuestionInputDTO, CreateQuestionsRequestDTO, UpdateAnswerInputDTO, UpdateQuestionRequestDTO, CollectionDataDTO, CollectionDetailDataDTO, CreateCollectionRequestDTO, IdDataDTO, UpdateCollectionRequestDTO, MarkDataDTO, SendFriendRequestDTO, FriendRequestDataDTO, RespondFriendRequestRequestDTO, FriendRequestListResponseDTO, PublicUserProfileDTO, ErrorDetailsDTO } from '../dtos.js';
import * as Enums from '../enums.js';

export class ProfileService {
  constructor(baseUrl, fetcher) {
    this.baseUrl = baseUrl;
    this.fetcher = fetcher;
  }

  async getProfile({ bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/profile`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => ProfileDataResponseDTO.fromJson(data));
  }

  async putProfile({ body, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/profile`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'PUT',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => ProfileDataResponseDTO.fromJson(data));
  }

}
