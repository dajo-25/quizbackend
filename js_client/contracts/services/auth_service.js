import { DTOResponse, DTOParams, EmptyParamsDTO, SearchQuestionsParamsDTO, GetQuestionParamsDTO, GetQuestionsBatchParamsDTO, UpdateQuestionParamsDTO, DeleteQuestionParamsDTO, UpdateCollectionParamsDTO, EmptyRequestDTO, BaseResponse, LoginResponseDTO, GenericResponseDTO, MessageResponseDTO, MustChangePasswordResponseDTO, UserStatusResponseDTO, ProfileDataResponseDTO, QuestionListResponseDTO, QuestionDataResponseDTO, CollectionListResponseDTO, IdDataResponseDTO, CollectionDetailResponseDTO, MarkListResponseDTO, FriendRequestResponseDTO, UserListResponseDTO, LoginRequestDTO, LoginDataDTO, SignupRequestDTO, MessageDataDTO, RecoverPasswordRequestDTO, VerifyEmailRequestDTO, MustChangePasswordDataDTO, ChangePasswordRequestDTO, UserStatusDataDTO, RegisterPushTokenRequestDTO, ProfileDataDTO, UpdateProfileRequestDTO, AnswerDataDTO, QuestionDataDTO, LocalizationDTO, CreateAnswerInputDTO, CreateQuestionInputDTO, CreateQuestionsRequestDTO, UpdateAnswerInputDTO, UpdateQuestionRequestDTO, CollectionDataDTO, CollectionDetailDataDTO, CreateCollectionRequestDTO, IdDataDTO, UpdateCollectionRequestDTO, MarkDataDTO, SendFriendRequestDTO, FriendRequestDataDTO, RespondFriendRequestRequestDTO, FriendRequestListResponseDTO, PublicUserProfileDTO, ErrorDetailsDTO } from '../dtos.js';
import * as Enums from '../enums.js';

export class AuthService {
  constructor(baseUrl, fetcher) {
    this.baseUrl = baseUrl;
    this.fetcher = fetcher;
  }

  async postLogin({ body,  } = {}) {
    const url = new URL(`${this.baseUrl}/auth/login`);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'POST',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => LoginResponseDTO.fromJson(data));
  }

  async postSignup({ body,  } = {}) {
    const url = new URL(`${this.baseUrl}/auth/signup`);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'POST',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => LoginResponseDTO.fromJson(data));
  }

  async postLogout({ bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/auth/logout`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'POST',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => GenericResponseDTO.fromJson(data));
  }

  async deleteAccount({ bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/auth/account`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'DELETE',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => MessageResponseDTO.fromJson(data));
  }

  async postRecover({ body,  } = {}) {
    const url = new URL(`${this.baseUrl}/auth/recover`);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'POST',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => MessageResponseDTO.fromJson(data));
  }

  async postVerify({ body,  } = {}) {
    const url = new URL(`${this.baseUrl}/auth/verify`);
    const headers = { 'Content-Type': 'application/json' };
    const response = await this.fetcher(url.toString(), {
      method: 'POST',
      headers,
      body: JSON.stringify(body.toJson())
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => GenericResponseDTO.fromJson(data));
  }

  async getMustChangePassword({ bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/auth/must-change-password`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => MustChangePasswordResponseDTO.fromJson(data));
  }

  async postChangePassword({ body, bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/auth/change-password`);
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

  async getStatus({ bearerToken } = {}) {
    const url = new URL(`${this.baseUrl}/auth/status`);
    const headers = { 'Content-Type': 'application/json' };
    if (bearerToken) headers['Authorization'] = `Bearer ${bearerToken}`;
    const response = await this.fetcher(url.toString(), {
      method: 'GET',
      headers,
    });
    const json = await response.json();
    return DTOResponse.fromJson(json, (data) => UserStatusResponseDTO.fromJson(data));
  }

}
