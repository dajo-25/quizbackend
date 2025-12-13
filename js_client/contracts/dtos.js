import * as Enums from './enums.js';

export class DTOParams {
  constructor(data = {}) {
  }

  static fromJson(json) {
    if (!json) return null;
    return new DTOParams({
    });
  }

  toJson() {
    return {
    };
  }
}

export class EmptyParamsDTO extends DTOParams {
  constructor(data = {}) {
    super(data);
  }

  static fromJson(json) {
    if (!json) return null;
    return new EmptyParamsDTO({
      ...json,
    });
  }

  toJson() {
    return {
      ...super.toJson(),
    };
  }
}

export class SearchQuestionsParamsDTO extends DTOParams {
  constructor(data = {}) {
    super(data);
    this.page = data.page;
    this.locale = data.locale;
  }

  static fromJson(json) {
    if (!json) return null;
    return new SearchQuestionsParamsDTO({
      ...json,
      page: json.page,
      locale: json.locale,
    });
  }

  toJson() {
    return {
      ...super.toJson(),
      page: this.page,
      locale: this.locale,
    };
  }
}

export class GetQuestionParamsDTO extends DTOParams {
  constructor(data = {}) {
    super(data);
    this.id = data.id;
    this.locale = data.locale;
  }

  static fromJson(json) {
    if (!json) return null;
    return new GetQuestionParamsDTO({
      ...json,
      id: json.id,
      locale: json.locale,
    });
  }

  toJson() {
    return {
      ...super.toJson(),
      id: this.id,
      locale: this.locale,
    };
  }
}

export class GetQuestionsBatchParamsDTO extends DTOParams {
  constructor(data = {}) {
    super(data);
    this.ids = data.ids;
    this.locale = data.locale;
  }

  static fromJson(json) {
    if (!json) return null;
    return new GetQuestionsBatchParamsDTO({
      ...json,
      ids: json.ids ? json.ids.map(e => e) : [],
      locale: json.locale,
    });
  }

  toJson() {
    return {
      ...super.toJson(),
      ids: this.ids ? this.ids.map(e => e) : [],
      locale: this.locale,
    };
  }
}

export class UpdateQuestionParamsDTO extends DTOParams {
  constructor(data = {}) {
    super(data);
    this.id = data.id;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UpdateQuestionParamsDTO({
      ...json,
      id: json.id,
    });
  }

  toJson() {
    return {
      ...super.toJson(),
      id: this.id,
    };
  }
}

export class DeleteQuestionParamsDTO extends DTOParams {
  constructor(data = {}) {
    super(data);
    this.id = data.id;
  }

  static fromJson(json) {
    if (!json) return null;
    return new DeleteQuestionParamsDTO({
      ...json,
      id: json.id,
    });
  }

  toJson() {
    return {
      ...super.toJson(),
      id: this.id,
    };
  }
}

export class UpdateCollectionParamsDTO extends DTOParams {
  constructor(data = {}) {
    super(data);
    this.id = data.id;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UpdateCollectionParamsDTO({
      ...json,
      id: json.id,
    });
  }

  toJson() {
    return {
      ...super.toJson(),
      id: this.id,
    };
  }
}

export class DTOResponse {
  constructor(data = {}) {
    this.success = data.success;
    this.data = data.data;
    this.message = data.message;
    this.error = data.error;
  }

  static fromJson(json, fromJsonT) {
    if (!json) return null;
    return new DTOResponse({
      success: json.success,
      data: fromJsonT(json.data),
      message: json.message,
      error: ErrorDetailsDTO.fromJson(json.error),
    });
  }

  toJson(toJsonT) {
    return {
      success: this.success,
      data: toJsonT(this.data),
      message: this.message,
      error: this.error && this.error.toJson ? this.error.toJson() : this.error,
    };
  }
}

export class EmptyRequestDTO {
  constructor(data = {}) {
  }

  static fromJson(json) {
    if (!json) return null;
    return new EmptyRequestDTO({
    });
  }

  toJson() {
    return {
    };
  }
}

export class BaseResponse {
  constructor(data = {}) {
    this.success = data.success;
    this.error = data.error;
  }

  static fromJson(json) {
    if (!json) return null;
    return new BaseResponse({
      success: json.success,
      error: json.error,
    });
  }

  toJson() {
    return {
      success: this.success,
      error: this.error,
    };
  }
}

export class LoginResponseDTO {
  constructor(data = {}) {
    this.token = data.token;
  }

  static fromJson(json) {
    if (!json) return null;
    return new LoginResponseDTO({
      token: json.token,
    });
  }

  toJson() {
    return {
      token: this.token,
    };
  }
}

export class GenericResponseDTO {
  constructor(data = {}) {
    this.success = data.success;
  }

  static fromJson(json) {
    if (!json) return null;
    return new GenericResponseDTO({
      success: json.success,
    });
  }

  toJson() {
    return {
      success: this.success,
    };
  }
}

export class MessageResponseDTO {
  constructor(data = {}) {
    this.message = data.message;
  }

  static fromJson(json) {
    if (!json) return null;
    return new MessageResponseDTO({
      message: json.message,
    });
  }

  toJson() {
    return {
      message: this.message,
    };
  }
}

export class MustChangePasswordResponseDTO {
  constructor(data = {}) {
    this.mustChange = data.mustChange;
  }

  static fromJson(json) {
    if (!json) return null;
    return new MustChangePasswordResponseDTO({
      mustChange: json.mustChange,
    });
  }

  toJson() {
    return {
      mustChange: this.mustChange,
    };
  }
}

export class UserStatusResponseDTO {
  constructor(data = {}) {
    this.status = data.status;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UserStatusResponseDTO({
      status: UserStatusDataDTO.fromJson(json.status),
    });
  }

  toJson() {
    return {
      status: this.status && this.status.toJson ? this.status.toJson() : this.status,
    };
  }
}

export class ProfileDataResponseDTO {
  constructor(data = {}) {
    this.profile = data.profile;
  }

  static fromJson(json) {
    if (!json) return null;
    return new ProfileDataResponseDTO({
      profile: ProfileDataDTO.fromJson(json.profile),
    });
  }

  toJson() {
    return {
      profile: this.profile && this.profile.toJson ? this.profile.toJson() : this.profile,
    };
  }
}

export class QuestionListResponseDTO {
  constructor(data = {}) {
    this.questions = data.questions;
  }

  static fromJson(json) {
    if (!json) return null;
    return new QuestionListResponseDTO({
      questions: json.questions ? json.questions.map(e => QuestionDataDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      questions: this.questions ? this.questions.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class QuestionDataResponseDTO {
  constructor(data = {}) {
    this.question = data.question;
  }

  static fromJson(json) {
    if (!json) return null;
    return new QuestionDataResponseDTO({
      question: QuestionDataDTO.fromJson(json.question),
    });
  }

  toJson() {
    return {
      question: this.question && this.question.toJson ? this.question.toJson() : this.question,
    };
  }
}

export class CollectionListResponseDTO {
  constructor(data = {}) {
    this.collections = data.collections;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CollectionListResponseDTO({
      collections: json.collections ? json.collections.map(e => CollectionDataDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      collections: this.collections ? this.collections.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class IdDataResponseDTO {
  constructor(data = {}) {
    this.id = data.id;
  }

  static fromJson(json) {
    if (!json) return null;
    return new IdDataResponseDTO({
      id: json.id,
    });
  }

  toJson() {
    return {
      id: this.id,
    };
  }
}

export class CollectionDetailResponseDTO {
  constructor(data = {}) {
    this.collection = data.collection;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CollectionDetailResponseDTO({
      collection: CollectionDetailDataDTO.fromJson(json.collection),
    });
  }

  toJson() {
    return {
      collection: this.collection && this.collection.toJson ? this.collection.toJson() : this.collection,
    };
  }
}

export class MarkListResponseDTO {
  constructor(data = {}) {
    this.marks = data.marks;
  }

  static fromJson(json) {
    if (!json) return null;
    return new MarkListResponseDTO({
      marks: json.marks ? json.marks.map(e => MarkDataDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      marks: this.marks ? this.marks.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class FriendRequestResponseDTO {
  constructor(data = {}) {
    this.friendRequest = data.friendRequest;
  }

  static fromJson(json) {
    if (!json) return null;
    return new FriendRequestResponseDTO({
      friendRequest: FriendRequestDataDTO.fromJson(json.friendRequest),
    });
  }

  toJson() {
    return {
      friendRequest: this.friendRequest && this.friendRequest.toJson ? this.friendRequest.toJson() : this.friendRequest,
    };
  }
}

export class UserListResponseDTO {
  constructor(data = {}) {
    this.users = data.users;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UserListResponseDTO({
      users: json.users ? json.users.map(e => PublicUserProfileDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      users: this.users ? this.users.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class LoginRequestDTO {
  constructor(data = {}) {
    this.email = data.email;
    this.passwordHash = data.passwordHash;
    this.uniqueId = data.uniqueId;
  }

  static fromJson(json) {
    if (!json) return null;
    return new LoginRequestDTO({
      email: json.email,
      passwordHash: json.passwordHash,
      uniqueId: json.uniqueId,
    });
  }

  toJson() {
    return {
      email: this.email,
      passwordHash: this.passwordHash,
      uniqueId: this.uniqueId,
    };
  }
}

export class LoginDataDTO {
  constructor(data = {}) {
    this.token = data.token;
    this.message = data.message;
  }

  static fromJson(json) {
    if (!json) return null;
    return new LoginDataDTO({
      token: json.token,
      message: json.message,
    });
  }

  toJson() {
    return {
      token: this.token,
      message: this.message,
    };
  }
}

export class SignupRequestDTO {
  constructor(data = {}) {
    this.email = data.email;
    this.username = data.username;
    this.name = data.name;
    this.surname = data.surname;
    this.passwordHash = data.passwordHash;
    this.uniqueId = data.uniqueId;
  }

  static fromJson(json) {
    if (!json) return null;
    return new SignupRequestDTO({
      email: json.email,
      username: json.username,
      name: json.name,
      surname: json.surname,
      passwordHash: json.passwordHash,
      uniqueId: json.uniqueId,
    });
  }

  toJson() {
    return {
      email: this.email,
      username: this.username,
      name: this.name,
      surname: this.surname,
      passwordHash: this.passwordHash,
      uniqueId: this.uniqueId,
    };
  }
}

export class MessageDataDTO {
  constructor(data = {}) {
    this.message = data.message;
  }

  static fromJson(json) {
    if (!json) return null;
    return new MessageDataDTO({
      message: json.message,
    });
  }

  toJson() {
    return {
      message: this.message,
    };
  }
}

export class RecoverPasswordRequestDTO {
  constructor(data = {}) {
    this.email = data.email;
  }

  static fromJson(json) {
    if (!json) return null;
    return new RecoverPasswordRequestDTO({
      email: json.email,
    });
  }

  toJson() {
    return {
      email: this.email,
    };
  }
}

export class VerifyEmailRequestDTO {
  constructor(data = {}) {
    this.code = data.code;
  }

  static fromJson(json) {
    if (!json) return null;
    return new VerifyEmailRequestDTO({
      code: json.code,
    });
  }

  toJson() {
    return {
      code: this.code,
    };
  }
}

export class MustChangePasswordDataDTO {
  constructor(data = {}) {
    this.must_change_password = data.must_change_password;
  }

  static fromJson(json) {
    if (!json) return null;
    return new MustChangePasswordDataDTO({
      must_change_password: json.must_change_password,
    });
  }

  toJson() {
    return {
      must_change_password: this.must_change_password,
    };
  }
}

export class ChangePasswordRequestDTO {
  constructor(data = {}) {
    this.oldHash = data.oldHash;
    this.newHash = data.newHash;
  }

  static fromJson(json) {
    if (!json) return null;
    return new ChangePasswordRequestDTO({
      oldHash: json.oldHash,
      newHash: json.newHash,
    });
  }

  toJson() {
    return {
      oldHash: this.oldHash,
      newHash: this.newHash,
    };
  }
}

export class UserStatusDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.email = data.email;
    this.username = data.username;
    this.isVerified = data.isVerified;
    this.mustChangePassword = data.mustChangePassword;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UserStatusDataDTO({
      id: json.id,
      email: json.email,
      username: json.username,
      isVerified: json.isVerified,
      mustChangePassword: json.mustChangePassword,
    });
  }

  toJson() {
    return {
      id: this.id,
      email: this.email,
      username: this.username,
      isVerified: this.isVerified,
      mustChangePassword: this.mustChangePassword,
    };
  }
}

export class RegisterPushTokenRequestDTO {
  constructor(data = {}) {
    this.pushToken = data.pushToken;
  }

  static fromJson(json) {
    if (!json) return null;
    return new RegisterPushTokenRequestDTO({
      pushToken: json.pushToken,
    });
  }

  toJson() {
    return {
      pushToken: this.pushToken,
    };
  }
}

export class ProfileDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.email = data.email;
    this.username = data.username;
    this.name = data.name;
    this.surname = data.surname;
    this.isVerified = data.isVerified;
  }

  static fromJson(json) {
    if (!json) return null;
    return new ProfileDataDTO({
      id: json.id,
      email: json.email,
      username: json.username,
      name: json.name,
      surname: json.surname,
      isVerified: json.isVerified,
    });
  }

  toJson() {
    return {
      id: this.id,
      email: this.email,
      username: this.username,
      name: this.name,
      surname: this.surname,
      isVerified: this.isVerified,
    };
  }
}

export class UpdateProfileRequestDTO {
  constructor(data = {}) {
    this.name = data.name;
    this.surname = data.surname;
    this.username = data.username;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UpdateProfileRequestDTO({
      name: json.name,
      surname: json.surname,
      username: json.username,
    });
  }

  toJson() {
    return {
      name: this.name,
      surname: this.surname,
      username: this.username,
    };
  }
}

export class AnswerDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.text = data.text;
    this.isCorrect = data.isCorrect;
  }

  static fromJson(json) {
    if (!json) return null;
    return new AnswerDataDTO({
      id: json.id,
      text: json.text,
      isCorrect: json.isCorrect,
    });
  }

  toJson() {
    return {
      id: this.id,
      text: this.text,
      isCorrect: this.isCorrect,
    };
  }
}

export class QuestionDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.text = data.text;
    this.answers = data.answers;
  }

  static fromJson(json) {
    if (!json) return null;
    return new QuestionDataDTO({
      id: json.id,
      text: json.text,
      answers: json.answers ? json.answers.map(e => AnswerDataDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      id: this.id,
      text: this.text,
      answers: this.answers ? this.answers.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class LocalizationDTO {
  constructor(data = {}) {
    this.locale = data.locale;
    this.text = data.text;
  }

  static fromJson(json) {
    if (!json) return null;
    return new LocalizationDTO({
      locale: json.locale,
      text: json.text,
    });
  }

  toJson() {
    return {
      locale: this.locale,
      text: this.text,
    };
  }
}

export class CreateAnswerInputDTO {
  constructor(data = {}) {
    this.localizations = data.localizations;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CreateAnswerInputDTO({
      localizations: json.localizations ? json.localizations.map(e => LocalizationDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      localizations: this.localizations ? this.localizations.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class CreateQuestionInputDTO {
  constructor(data = {}) {
    this.localizations = data.localizations;
    this.answers = data.answers;
    this.correctAnswersIndices = data.correctAnswersIndices;
    this.isDiscoverable = data.isDiscoverable;
    this.collectionIds = data.collectionIds;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CreateQuestionInputDTO({
      localizations: json.localizations ? json.localizations.map(e => LocalizationDTO.fromJson(e)) : [],
      answers: json.answers ? json.answers.map(e => CreateAnswerInputDTO.fromJson(e)) : [],
      correctAnswersIndices: json.correctAnswersIndices ? json.correctAnswersIndices.map(e => e) : [],
      isDiscoverable: json.isDiscoverable,
      collectionIds: json.collectionIds ? json.collectionIds.map(e => e) : [],
    });
  }

  toJson() {
    return {
      localizations: this.localizations ? this.localizations.map(e => e && e.toJson ? e.toJson() : e) : [],
      answers: this.answers ? this.answers.map(e => e && e.toJson ? e.toJson() : e) : [],
      correctAnswersIndices: this.correctAnswersIndices ? this.correctAnswersIndices.map(e => e) : [],
      isDiscoverable: this.isDiscoverable,
      collectionIds: this.collectionIds ? this.collectionIds.map(e => e) : [],
    };
  }
}

export class CreateQuestionsRequestDTO {
  constructor(data = {}) {
    this.questions = data.questions;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CreateQuestionsRequestDTO({
      questions: json.questions ? json.questions.map(e => CreateQuestionInputDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      questions: this.questions ? this.questions.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class UpdateAnswerInputDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.localizations = data.localizations;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UpdateAnswerInputDTO({
      id: json.id,
      localizations: json.localizations ? json.localizations.map(e => LocalizationDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      id: this.id,
      localizations: this.localizations ? this.localizations.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class UpdateQuestionRequestDTO {
  constructor(data = {}) {
    this.localizations = data.localizations;
    this.answers = data.answers;
    this.correctAnswersIndices = data.correctAnswersIndices;
    this.isDiscoverable = data.isDiscoverable;
    this.collectionIds = data.collectionIds;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UpdateQuestionRequestDTO({
      localizations: json.localizations ? json.localizations.map(e => LocalizationDTO.fromJson(e)) : [],
      answers: json.answers ? json.answers.map(e => UpdateAnswerInputDTO.fromJson(e)) : [],
      correctAnswersIndices: json.correctAnswersIndices ? json.correctAnswersIndices.map(e => e) : [],
      isDiscoverable: json.isDiscoverable,
      collectionIds: json.collectionIds ? json.collectionIds.map(e => e) : [],
    });
  }

  toJson() {
    return {
      localizations: this.localizations ? this.localizations.map(e => e && e.toJson ? e.toJson() : e) : [],
      answers: this.answers ? this.answers.map(e => e && e.toJson ? e.toJson() : e) : [],
      correctAnswersIndices: this.correctAnswersIndices ? this.correctAnswersIndices.map(e => e) : [],
      isDiscoverable: this.isDiscoverable,
      collectionIds: this.collectionIds ? this.collectionIds.map(e => e) : [],
    };
  }
}

export class CollectionDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.name = data.name;
    this.description = data.description;
    this.isPublic = data.isPublic;
    this.creatorId = data.creatorId;
    this.createdAt = data.createdAt;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CollectionDataDTO({
      id: json.id,
      name: json.name,
      description: json.description,
      isPublic: json.isPublic,
      creatorId: json.creatorId,
      createdAt: json.createdAt,
    });
  }

  toJson() {
    return {
      id: this.id,
      name: this.name,
      description: this.description,
      isPublic: this.isPublic,
      creatorId: this.creatorId,
      createdAt: this.createdAt,
    };
  }
}

export class CollectionDetailDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.name = data.name;
    this.description = data.description;
    this.isPublic = data.isPublic;
    this.creatorId = data.creatorId;
    this.createdAt = data.createdAt;
    this.questionIds = data.questionIds;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CollectionDetailDataDTO({
      id: json.id,
      name: json.name,
      description: json.description,
      isPublic: json.isPublic,
      creatorId: json.creatorId,
      createdAt: json.createdAt,
      questionIds: json.questionIds ? json.questionIds.map(e => e) : [],
    });
  }

  toJson() {
    return {
      id: this.id,
      name: this.name,
      description: this.description,
      isPublic: this.isPublic,
      creatorId: this.creatorId,
      createdAt: this.createdAt,
      questionIds: this.questionIds ? this.questionIds.map(e => e) : [],
    };
  }
}

export class CreateCollectionRequestDTO {
  constructor(data = {}) {
    this.name = data.name;
    this.description = data.description;
    this.isPublic = data.isPublic;
    this.questionIds = data.questionIds;
  }

  static fromJson(json) {
    if (!json) return null;
    return new CreateCollectionRequestDTO({
      name: json.name,
      description: json.description,
      isPublic: json.isPublic,
      questionIds: json.questionIds ? json.questionIds.map(e => e) : [],
    });
  }

  toJson() {
    return {
      name: this.name,
      description: this.description,
      isPublic: this.isPublic,
      questionIds: this.questionIds ? this.questionIds.map(e => e) : [],
    };
  }
}

export class IdDataDTO {
  constructor(data = {}) {
    this.id = data.id;
  }

  static fromJson(json) {
    if (!json) return null;
    return new IdDataDTO({
      id: json.id,
    });
  }

  toJson() {
    return {
      id: this.id,
    };
  }
}

export class UpdateCollectionRequestDTO {
  constructor(data = {}) {
    this.name = data.name;
    this.description = data.description;
    this.isPublic = data.isPublic;
    this.questionIds = data.questionIds;
  }

  static fromJson(json) {
    if (!json) return null;
    return new UpdateCollectionRequestDTO({
      name: json.name,
      description: json.description,
      isPublic: json.isPublic,
      questionIds: json.questionIds ? json.questionIds.map(e => e) : [],
    });
  }

  toJson() {
    return {
      name: this.name,
      description: this.description,
      isPublic: this.isPublic,
      questionIds: this.questionIds ? this.questionIds.map(e => e) : [],
    };
  }
}

export class MarkDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.questionId = data.questionId;
    this.isCorrect = data.isCorrect;
    this.createdAt = data.createdAt;
  }

  static fromJson(json) {
    if (!json) return null;
    return new MarkDataDTO({
      id: json.id,
      questionId: json.questionId,
      isCorrect: json.isCorrect,
      createdAt: json.createdAt,
    });
  }

  toJson() {
    return {
      id: this.id,
      questionId: this.questionId,
      isCorrect: this.isCorrect,
      createdAt: this.createdAt,
    };
  }
}

export class SendFriendRequestDTO {
  constructor(data = {}) {
    this.targetUserId = data.targetUserId;
  }

  static fromJson(json) {
    if (!json) return null;
    return new SendFriendRequestDTO({
      targetUserId: json.targetUserId,
    });
  }

  toJson() {
    return {
      targetUserId: this.targetUserId,
    };
  }
}

export class FriendRequestDataDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.senderId = data.senderId;
    this.receiverId = data.receiverId;
    this.status = data.status;
    this.createdAt = data.createdAt;
    this.sender = data.sender;
    this.receiver = data.receiver;
  }

  static fromJson(json) {
    if (!json) return null;
    return new FriendRequestDataDTO({
      id: json.id,
      senderId: json.senderId,
      receiverId: json.receiverId,
      status: json.status,
      createdAt: json.createdAt,
      sender: PublicUserProfileDTO.fromJson(json.sender),
      receiver: PublicUserProfileDTO.fromJson(json.receiver),
    });
  }

  toJson() {
    return {
      id: this.id,
      senderId: this.senderId,
      receiverId: this.receiverId,
      status: this.status,
      createdAt: this.createdAt,
      sender: this.sender && this.sender.toJson ? this.sender.toJson() : this.sender,
      receiver: this.receiver && this.receiver.toJson ? this.receiver.toJson() : this.receiver,
    };
  }
}

export class RespondFriendRequestRequestDTO {
  constructor(data = {}) {
    this.requestId = data.requestId;
    this.accept = data.accept;
  }

  static fromJson(json) {
    if (!json) return null;
    return new RespondFriendRequestRequestDTO({
      requestId: json.requestId,
      accept: json.accept,
    });
  }

  toJson() {
    return {
      requestId: this.requestId,
      accept: this.accept,
    };
  }
}

export class FriendRequestListResponseDTO {
  constructor(data = {}) {
    this.requests = data.requests;
  }

  static fromJson(json) {
    if (!json) return null;
    return new FriendRequestListResponseDTO({
      requests: json.requests ? json.requests.map(e => FriendRequestDataDTO.fromJson(e)) : [],
    });
  }

  toJson() {
    return {
      requests: this.requests ? this.requests.map(e => e && e.toJson ? e.toJson() : e) : [],
    };
  }
}

export class PublicUserProfileDTO {
  constructor(data = {}) {
    this.id = data.id;
    this.username = data.username;
    this.name = data.name;
    this.surname = data.surname;
  }

  static fromJson(json) {
    if (!json) return null;
    return new PublicUserProfileDTO({
      id: json.id,
      username: json.username,
      name: json.name,
      surname: json.surname,
    });
  }

  toJson() {
    return {
      id: this.id,
      username: this.username,
      name: this.name,
      surname: this.surname,
    };
  }
}

export class ErrorDetailsDTO {
  constructor(data = {}) {
    this.type = data.type;
    this.message = data.message;
  }

  static fromJson(json) {
    if (!json) return null;
    return new ErrorDetailsDTO({
      type: json.type,
      message: json.message,
    });
  }

  toJson() {
    return {
      type: this.type && this.type.toJson ? this.type.toJson() : this.type,
      message: this.message,
    };
  }
}
