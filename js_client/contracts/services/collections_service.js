import { DTOResponse, DTOParams, EmptyParamsDTO, SearchQuestionsParamsDTO, GetQuestionParamsDTO, GetQuestionsBatchParamsDTO, UpdateQuestionParamsDTO, DeleteQuestionParamsDTO, UpdateCollectionParamsDTO, EmptyRequestDTO, BaseResponse, LoginResponseDTO, GenericResponseDTO, MessageResponseDTO, MustChangePasswordResponseDTO, UserStatusResponseDTO, ProfileDataResponseDTO, QuestionListResponseDTO, QuestionDataResponseDTO, CollectionListResponseDTO, IdDataResponseDTO, CollectionDetailResponseDTO, MarkListResponseDTO, FriendRequestResponseDTO, UserListResponseDTO, LoginRequestDTO, LoginDataDTO, SignupRequestDTO, MessageDataDTO, RecoverPasswordRequestDTO, VerifyEmailRequestDTO, MustChangePasswordDataDTO, ChangePasswordRequestDTO, UserStatusDataDTO, RegisterPushTokenRequestDTO, ProfileDataDTO, UpdateProfileRequestDTO, AnswerDataDTO, QuestionDataDTO, LocalizationDTO, CreateAnswerInputDTO, CreateQuestionInputDTO, CreateQuestionsRequestDTO, UpdateAnswerInputDTO, UpdateQuestionRequestDTO, CollectionDataDTO, CollectionDetailDataDTO, CreateCollectionRequestDTO, IdDataDTO, UpdateCollectionRequestDTO, MarkDataDTO, SendFriendRequestDTO, FriendRequestDataDTO, RespondFriendRequestRequestDTO, FriendRequestListResponseDTO, PublicUserProfileDTO, ErrorDetailsDTO } from '../dtos.js';
import * as Enums from '../enums.js';

export class CollectionsService {
  constructor(baseUrl, fetcher) {
    this.baseUrl = baseUrl;
    this.fetcher = fetcher;
  }

  async getCollections({  } = {}) {
    const url = new URL(`${this.baseUrl}/collections`);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => CollectionListResponseDTO.fromJson(data));
  }

  async postCollections({ body, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/collections`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'POST',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => IdDataResponseDTO.fromJson(data));
  }

  async getCollectionsId({ id,  } = {}) {
    const url = new URL(`${this.baseUrl}/collections/${encodeURIComponent(id)}`);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => CollectionDetailResponseDTO.fromJson(data));
  }

  async putCollectionsId({ body, id, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/collections/${encodeURIComponent(id)}`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'PUT',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => GenericResponseDTO.fromJson(data));
  }

  async deleteCollectionsId({ id, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/collections/${encodeURIComponent(id)}`);
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
