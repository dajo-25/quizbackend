import { DTOResponse, DTOParams, EmptyParamsDTO, SearchQuestionsParamsDTO, GetQuestionParamsDTO, GetQuestionsBatchParamsDTO, UpdateQuestionParamsDTO, DeleteQuestionParamsDTO, UpdateCollectionParamsDTO, EmptyRequestDTO, BaseResponse, LoginResponseDTO, GenericResponseDTO, MessageResponseDTO, MustChangePasswordResponseDTO, UserStatusResponseDTO, ProfileDataResponseDTO, QuestionListResponseDTO, QuestionDataResponseDTO, CollectionListResponseDTO, IdDataResponseDTO, CollectionDetailResponseDTO, MarkListResponseDTO, FriendRequestResponseDTO, UserListResponseDTO, LoginRequestDTO, LoginDataDTO, SignupRequestDTO, MessageDataDTO, RecoverPasswordRequestDTO, VerifyEmailRequestDTO, MustChangePasswordDataDTO, ChangePasswordRequestDTO, UserStatusDataDTO, RegisterPushTokenRequestDTO, ProfileDataDTO, UpdateProfileRequestDTO, AnswerDataDTO, QuestionDataDTO, LocalizationDTO, CreateAnswerInputDTO, CreateQuestionInputDTO, CreateQuestionsRequestDTO, UpdateAnswerInputDTO, UpdateQuestionRequestDTO, CollectionDataDTO, CollectionDetailDataDTO, CreateCollectionRequestDTO, IdDataDTO, UpdateCollectionRequestDTO, MarkDataDTO, SendFriendRequestDTO, FriendRequestDataDTO, RespondFriendRequestRequestDTO, FriendRequestListResponseDTO, PublicUserProfileDTO, ErrorDetailsDTO } from '../dtos.js';
import * as Enums from '../enums.js';

export class DevicesService {
  constructor(baseUrl, fetcher) {
    this.baseUrl = baseUrl;
    this.fetcher = fetcher;
  }

  async postPushToken({ body, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/devices/push-token`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'POST',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => GenericResponseDTO.fromJson(data));
  }

  async deletePushToken({ bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/devices/push-token`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'DELETE',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => GenericResponseDTO.fromJson(data));
  }

}
