import { DTOResponse, DTOParams, EmptyParamsDTO, SearchQuestionsParamsDTO, GetQuestionParamsDTO, GetQuestionsBatchParamsDTO, UpdateQuestionParamsDTO, DeleteQuestionParamsDTO, UpdateCollectionParamsDTO, EmptyRequestDTO, BaseResponse, LoginResponseDTO, GenericResponseDTO, MessageResponseDTO, MustChangePasswordResponseDTO, UserStatusResponseDTO, ProfileDataResponseDTO, QuestionListResponseDTO, QuestionDataResponseDTO, CollectionListResponseDTO, IdDataResponseDTO, CollectionDetailResponseDTO, MarkListResponseDTO, FriendRequestResponseDTO, UserListResponseDTO, LoginRequestDTO, LoginDataDTO, SignupRequestDTO, MessageDataDTO, RecoverPasswordRequestDTO, VerifyEmailRequestDTO, MustChangePasswordDataDTO, ChangePasswordRequestDTO, UserStatusDataDTO, RegisterPushTokenRequestDTO, ProfileDataDTO, UpdateProfileRequestDTO, AnswerDataDTO, QuestionDataDTO, LocalizationDTO, CreateAnswerInputDTO, CreateQuestionInputDTO, CreateQuestionsRequestDTO, UpdateAnswerInputDTO, UpdateQuestionRequestDTO, CollectionDataDTO, CollectionDetailDataDTO, CreateCollectionRequestDTO, IdDataDTO, UpdateCollectionRequestDTO, MarkDataDTO, SendFriendRequestDTO, FriendRequestDataDTO, RespondFriendRequestRequestDTO, FriendRequestListResponseDTO, PublicUserProfileDTO, ErrorDetailsDTO } from '../dtos.js';
import * as Enums from '../enums.js';

export class QuestionsService {
  constructor(baseUrl, fetcher) {
    this.baseUrl = baseUrl;
    this.fetcher = fetcher;
  }

  async getQuestions({ page, locale,  } = {}) {
    const url = new URL(`${this.baseUrl}/questions`);
    if (page !== undefined && page !== null) url.searchParams.append('page', page);
    if (locale !== undefined && locale !== null) url.searchParams.append('locale', locale);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => QuestionListResponseDTO.fromJson(data));
  }

  async postQuestions({ body, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/questions`);
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

  async getQuestionsId({ id, locale,  } = {}) {
    const url = new URL(`${this.baseUrl}/questions/${encodeURIComponent(id)}`);
    if (locale !== undefined && locale !== null) url.searchParams.append('locale', locale);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => QuestionDataResponseDTO.fromJson(data));
  }

  async putQuestionsId({ body, id, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/questions/${encodeURIComponent(id)}`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'PUT',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => QuestionDataResponseDTO.fromJson(data));
  }

  async deleteQuestionsId({ id, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/questions/${encodeURIComponent(id)}`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'DELETE',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => GenericResponseDTO.fromJson(data));
  }

  async getBatch({ ids, locale,  } = {}) {
    const url = new URL(`${this.baseUrl}/questions/batch`);
    if (ids !== undefined && ids !== null) url.searchParams.append('ids', ids);
    if (locale !== undefined && locale !== null) url.searchParams.append('locale', locale);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => QuestionListResponseDTO.fromJson(data));
  }

}
