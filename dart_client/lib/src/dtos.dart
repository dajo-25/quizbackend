import 'package:json_annotation/json_annotation.dart';
import 'enums.dart';

part 'dtos.g.dart';

@JsonSerializable()
class DTOParams {

  DTOParams({});

  factory DTOParams.fromJson(Map<String, dynamic> json) => _$DTOParamsFromJson(json);
  Map<String, dynamic> toJson() => _$DTOParamsToJson(this);
}

@JsonSerializable()
class EmptyParamsDTO extends DTOParams {

  EmptyParamsDTO({});

  factory EmptyParamsDTO.fromJson(Map<String, dynamic> json) => _$EmptyParamsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$EmptyParamsDTOToJson(this);
}

@JsonSerializable()
class SearchQuestionsParamsDTO extends DTOParams {
  final int page;

  SearchQuestionsParamsDTO({required this.page, });

  factory SearchQuestionsParamsDTO.fromJson(Map<String, dynamic> json) => _$SearchQuestionsParamsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$SearchQuestionsParamsDTOToJson(this);
}

@JsonSerializable()
class GetQuestionParamsDTO extends DTOParams {
  final int id;

  GetQuestionParamsDTO({required this.id, });

  factory GetQuestionParamsDTO.fromJson(Map<String, dynamic> json) => _$GetQuestionParamsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$GetQuestionParamsDTOToJson(this);
}

@JsonSerializable()
class GetQuestionsBatchParamsDTO extends DTOParams {
  final List<int> ids;

  GetQuestionsBatchParamsDTO({required this.ids, });

  factory GetQuestionsBatchParamsDTO.fromJson(Map<String, dynamic> json) => _$GetQuestionsBatchParamsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$GetQuestionsBatchParamsDTOToJson(this);
}

@JsonSerializable()
class UpdateQuestionParamsDTO extends DTOParams {
  final int id;

  UpdateQuestionParamsDTO({required this.id, });

  factory UpdateQuestionParamsDTO.fromJson(Map<String, dynamic> json) => _$UpdateQuestionParamsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UpdateQuestionParamsDTOToJson(this);
}

@JsonSerializable()
class DeleteQuestionParamsDTO extends DTOParams {
  final int id;

  DeleteQuestionParamsDTO({required this.id, });

  factory DeleteQuestionParamsDTO.fromJson(Map<String, dynamic> json) => _$DeleteQuestionParamsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$DeleteQuestionParamsDTOToJson(this);
}

@JsonSerializable()
class UpdateCollectionParamsDTO extends DTOParams {
  final int id;

  UpdateCollectionParamsDTO({required this.id, });

  factory UpdateCollectionParamsDTO.fromJson(Map<String, dynamic> json) => _$UpdateCollectionParamsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UpdateCollectionParamsDTOToJson(this);
}

@JsonSerializable(genericArgumentFactories: true)
class DTOResponse<T> {
  final bool success;
  final T? data;
  final String? message;
  final ErrorDetailsDTO? error;

  DTOResponse<T>({required this.success, this.data, this.message, this.error, });

  factory DTOResponse.fromJson(Map<String, dynamic> json, T Function(Object? json) fromJsonT) => _$DTOResponseFromJson(json, fromJsonT);
  Map<String, dynamic> toJson(Object? Function(T value) toJsonT) => _$DTOResponseToJson(this, toJsonT);
}

@JsonSerializable()
class EmptyRequestDTO {

  EmptyRequestDTO({});

  factory EmptyRequestDTO.fromJson(Map<String, dynamic> json) => _$EmptyRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$EmptyRequestDTOToJson(this);
}

@JsonSerializable()
class BaseResponse {
  final bool success;
  final String error;

  BaseResponse({required this.success, required this.error, });

  factory BaseResponse.fromJson(Map<String, dynamic> json) => _$BaseResponseFromJson(json);
  Map<String, dynamic> toJson() => _$BaseResponseToJson(this);
}

@JsonSerializable()
class LoginResponseDTO {
  final String token;

  LoginResponseDTO({required this.token, });

  factory LoginResponseDTO.fromJson(Map<String, dynamic> json) => _$LoginResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$LoginResponseDTOToJson(this);
}

@JsonSerializable()
class GenericResponseDTO {
  final bool success;

  GenericResponseDTO({required this.success, });

  factory GenericResponseDTO.fromJson(Map<String, dynamic> json) => _$GenericResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$GenericResponseDTOToJson(this);
}

@JsonSerializable()
class MessageResponseDTO {
  final String message;

  MessageResponseDTO({required this.message, });

  factory MessageResponseDTO.fromJson(Map<String, dynamic> json) => _$MessageResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$MessageResponseDTOToJson(this);
}

@JsonSerializable()
class MustChangePasswordResponseDTO {
  final bool mustChange;

  MustChangePasswordResponseDTO({required this.mustChange, });

  factory MustChangePasswordResponseDTO.fromJson(Map<String, dynamic> json) => _$MustChangePasswordResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$MustChangePasswordResponseDTOToJson(this);
}

@JsonSerializable()
class UserStatusResponseDTO {
  final UserStatusDataDTO? status;

  UserStatusResponseDTO({this.status, });

  factory UserStatusResponseDTO.fromJson(Map<String, dynamic> json) => _$UserStatusResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UserStatusResponseDTOToJson(this);
}

@JsonSerializable()
class ProfileDataResponseDTO {
  final ProfileDataDTO profile;

  ProfileDataResponseDTO({required this.profile, });

  factory ProfileDataResponseDTO.fromJson(Map<String, dynamic> json) => _$ProfileDataResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$ProfileDataResponseDTOToJson(this);
}

@JsonSerializable()
class QuestionListResponseDTO {
  final List<QuestionDataDTO> questions;

  QuestionListResponseDTO({required this.questions, });

  factory QuestionListResponseDTO.fromJson(Map<String, dynamic> json) => _$QuestionListResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$QuestionListResponseDTOToJson(this);
}

@JsonSerializable()
class QuestionDataResponseDTO {
  final QuestionDataDTO question;

  QuestionDataResponseDTO({required this.question, });

  factory QuestionDataResponseDTO.fromJson(Map<String, dynamic> json) => _$QuestionDataResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$QuestionDataResponseDTOToJson(this);
}

@JsonSerializable()
class CollectionListResponseDTO {
  final List<CollectionDataDTO> collections;

  CollectionListResponseDTO({required this.collections, });

  factory CollectionListResponseDTO.fromJson(Map<String, dynamic> json) => _$CollectionListResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CollectionListResponseDTOToJson(this);
}

@JsonSerializable()
class IdDataResponseDTO {
  final int id;

  IdDataResponseDTO({required this.id, });

  factory IdDataResponseDTO.fromJson(Map<String, dynamic> json) => _$IdDataResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$IdDataResponseDTOToJson(this);
}

@JsonSerializable()
class CollectionDetailResponseDTO {
  final CollectionDetailDataDTO? collection;

  CollectionDetailResponseDTO({this.collection, });

  factory CollectionDetailResponseDTO.fromJson(Map<String, dynamic> json) => _$CollectionDetailResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CollectionDetailResponseDTOToJson(this);
}

@JsonSerializable()
class MarkListResponseDTO {
  final List<MarkDataDTO> marks;

  MarkListResponseDTO({required this.marks, });

  factory MarkListResponseDTO.fromJson(Map<String, dynamic> json) => _$MarkListResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$MarkListResponseDTOToJson(this);
}

@JsonSerializable()
class FriendRequestResponseDTO {
  final FriendRequestDataDTO? friendRequest;

  FriendRequestResponseDTO({this.friendRequest, });

  factory FriendRequestResponseDTO.fromJson(Map<String, dynamic> json) => _$FriendRequestResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$FriendRequestResponseDTOToJson(this);
}

@JsonSerializable()
class UserListResponseDTO {
  final List<PublicUserProfileDTO> users;

  UserListResponseDTO({required this.users, });

  factory UserListResponseDTO.fromJson(Map<String, dynamic> json) => _$UserListResponseDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UserListResponseDTOToJson(this);
}

@JsonSerializable()
class LoginRequestDTO {
  final String email;
  final String passwordHash;
  final String uniqueId;

  LoginRequestDTO({required this.email, required this.passwordHash, required this.uniqueId, });

  factory LoginRequestDTO.fromJson(Map<String, dynamic> json) => _$LoginRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$LoginRequestDTOToJson(this);
}

@JsonSerializable()
class LoginDataDTO {
  final String token;
  final String message;

  LoginDataDTO({required this.token, required this.message, });

  factory LoginDataDTO.fromJson(Map<String, dynamic> json) => _$LoginDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$LoginDataDTOToJson(this);
}

@JsonSerializable()
class SignupRequestDTO {
  final String email;
  final String username;
  final String name;
  final String surname;
  final String passwordHash;
  final String uniqueId;

  SignupRequestDTO({required this.email, required this.username, required this.name, required this.surname, required this.passwordHash, required this.uniqueId, });

  factory SignupRequestDTO.fromJson(Map<String, dynamic> json) => _$SignupRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$SignupRequestDTOToJson(this);
}

@JsonSerializable()
class MessageDataDTO {
  final String message;

  MessageDataDTO({required this.message, });

  factory MessageDataDTO.fromJson(Map<String, dynamic> json) => _$MessageDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$MessageDataDTOToJson(this);
}

@JsonSerializable()
class RecoverPasswordRequestDTO {
  final String email;

  RecoverPasswordRequestDTO({required this.email, });

  factory RecoverPasswordRequestDTO.fromJson(Map<String, dynamic> json) => _$RecoverPasswordRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$RecoverPasswordRequestDTOToJson(this);
}

@JsonSerializable()
class VerifyEmailRequestDTO {
  final String code;

  VerifyEmailRequestDTO({required this.code, });

  factory VerifyEmailRequestDTO.fromJson(Map<String, dynamic> json) => _$VerifyEmailRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$VerifyEmailRequestDTOToJson(this);
}

@JsonSerializable()
class MustChangePasswordDataDTO {
  final bool must_change_password;

  MustChangePasswordDataDTO({required this.must_change_password, });

  factory MustChangePasswordDataDTO.fromJson(Map<String, dynamic> json) => _$MustChangePasswordDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$MustChangePasswordDataDTOToJson(this);
}

@JsonSerializable()
class ChangePasswordRequestDTO {
  final String oldHash;
  final String newHash;

  ChangePasswordRequestDTO({required this.oldHash, required this.newHash, });

  factory ChangePasswordRequestDTO.fromJson(Map<String, dynamic> json) => _$ChangePasswordRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$ChangePasswordRequestDTOToJson(this);
}

@JsonSerializable()
class UserStatusDataDTO {
  final int? id;
  final String? email;
  final String? username;
  final bool isVerified;
  final bool mustChangePassword;

  UserStatusDataDTO({this.id, this.email, this.username, required this.isVerified, required this.mustChangePassword, });

  factory UserStatusDataDTO.fromJson(Map<String, dynamic> json) => _$UserStatusDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UserStatusDataDTOToJson(this);
}

@JsonSerializable()
class RegisterPushTokenRequestDTO {
  final String pushToken;

  RegisterPushTokenRequestDTO({required this.pushToken, });

  factory RegisterPushTokenRequestDTO.fromJson(Map<String, dynamic> json) => _$RegisterPushTokenRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$RegisterPushTokenRequestDTOToJson(this);
}

@JsonSerializable()
class ProfileDataDTO {
  final int id;
  final String email;
  final String username;
  final String name;
  final String surname;
  final bool? isVerified;

  ProfileDataDTO({required this.id, required this.email, required this.username, required this.name, required this.surname, this.isVerified, });

  factory ProfileDataDTO.fromJson(Map<String, dynamic> json) => _$ProfileDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$ProfileDataDTOToJson(this);
}

@JsonSerializable()
class UpdateProfileRequestDTO {
  final String name;
  final String surname;
  final String username;

  UpdateProfileRequestDTO({required this.name, required this.surname, required this.username, });

  factory UpdateProfileRequestDTO.fromJson(Map<String, dynamic> json) => _$UpdateProfileRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UpdateProfileRequestDTOToJson(this);
}

@JsonSerializable()
class AnswerDataDTO {
  final int id;
  final String text;
  final bool isCorrect;

  AnswerDataDTO({required this.id, required this.text, required this.isCorrect, });

  factory AnswerDataDTO.fromJson(Map<String, dynamic> json) => _$AnswerDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$AnswerDataDTOToJson(this);
}

@JsonSerializable()
class QuestionDataDTO {
  final int id;
  final String text;
  final List<AnswerDataDTO> answers;

  QuestionDataDTO({required this.id, required this.text, required this.answers, });

  factory QuestionDataDTO.fromJson(Map<String, dynamic> json) => _$QuestionDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$QuestionDataDTOToJson(this);
}

@JsonSerializable()
class LocalizationDTO {
  final String locale;
  final String text;

  LocalizationDTO({required this.locale, required this.text, });

  factory LocalizationDTO.fromJson(Map<String, dynamic> json) => _$LocalizationDTOFromJson(json);
  Map<String, dynamic> toJson() => _$LocalizationDTOToJson(this);
}

@JsonSerializable()
class CreateAnswerInputDTO {
  final List<LocalizationDTO> localizations;

  CreateAnswerInputDTO({required this.localizations, });

  factory CreateAnswerInputDTO.fromJson(Map<String, dynamic> json) => _$CreateAnswerInputDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CreateAnswerInputDTOToJson(this);
}

@JsonSerializable()
class CreateQuestionInputDTO {
  final List<LocalizationDTO> localizations;
  final List<CreateAnswerInputDTO> answers;
  final List<int> correctAnswersIndices;
  final bool isDiscoverable;
  final List<int> collectionIds;

  CreateQuestionInputDTO({required this.localizations, required this.answers, required this.correctAnswersIndices, required this.isDiscoverable, required this.collectionIds, });

  factory CreateQuestionInputDTO.fromJson(Map<String, dynamic> json) => _$CreateQuestionInputDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CreateQuestionInputDTOToJson(this);
}

@JsonSerializable()
class CreateQuestionsRequestDTO {
  final List<CreateQuestionInputDTO> questions;

  CreateQuestionsRequestDTO({required this.questions, });

  factory CreateQuestionsRequestDTO.fromJson(Map<String, dynamic> json) => _$CreateQuestionsRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CreateQuestionsRequestDTOToJson(this);
}

@JsonSerializable()
class UpdateAnswerInputDTO {
  final int id;
  final List<LocalizationDTO> localizations;

  UpdateAnswerInputDTO({required this.id, required this.localizations, });

  factory UpdateAnswerInputDTO.fromJson(Map<String, dynamic> json) => _$UpdateAnswerInputDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UpdateAnswerInputDTOToJson(this);
}

@JsonSerializable()
class UpdateQuestionRequestDTO {
  final List<LocalizationDTO> localizations;
  final List<UpdateAnswerInputDTO> answers;
  final List<int> correctAnswersIndices;
  final bool isDiscoverable;
  final List<int> collectionIds;

  UpdateQuestionRequestDTO({required this.localizations, required this.answers, required this.correctAnswersIndices, required this.isDiscoverable, required this.collectionIds, });

  factory UpdateQuestionRequestDTO.fromJson(Map<String, dynamic> json) => _$UpdateQuestionRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UpdateQuestionRequestDTOToJson(this);
}

@JsonSerializable()
class CollectionDataDTO {
  final int id;
  final String name;
  final String description;
  final bool isPublic;
  final int creatorId;
  final String createdAt;

  CollectionDataDTO({required this.id, required this.name, required this.description, required this.isPublic, required this.creatorId, required this.createdAt, });

  factory CollectionDataDTO.fromJson(Map<String, dynamic> json) => _$CollectionDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CollectionDataDTOToJson(this);
}

@JsonSerializable()
class CollectionDetailDataDTO {
  final int id;
  final String name;
  final String description;
  final bool isPublic;
  final int creatorId;
  final String createdAt;
  final List<int> questionIds;

  CollectionDetailDataDTO({required this.id, required this.name, required this.description, required this.isPublic, required this.creatorId, required this.createdAt, required this.questionIds, });

  factory CollectionDetailDataDTO.fromJson(Map<String, dynamic> json) => _$CollectionDetailDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CollectionDetailDataDTOToJson(this);
}

@JsonSerializable()
class CreateCollectionRequestDTO {
  final String name;
  final String description;
  final bool isPublic;

  CreateCollectionRequestDTO({required this.name, required this.description, required this.isPublic, });

  factory CreateCollectionRequestDTO.fromJson(Map<String, dynamic> json) => _$CreateCollectionRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$CreateCollectionRequestDTOToJson(this);
}

@JsonSerializable()
class IdDataDTO {
  final int id;

  IdDataDTO({required this.id, });

  factory IdDataDTO.fromJson(Map<String, dynamic> json) => _$IdDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$IdDataDTOToJson(this);
}

@JsonSerializable()
class UpdateCollectionRequestDTO {
  final String name;
  final String description;
  final bool isPublic;

  UpdateCollectionRequestDTO({required this.name, required this.description, required this.isPublic, });

  factory UpdateCollectionRequestDTO.fromJson(Map<String, dynamic> json) => _$UpdateCollectionRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UpdateCollectionRequestDTOToJson(this);
}

@JsonSerializable()
class MarkDataDTO {
  final int id;
  final int questionId;
  final bool isCorrect;
  final String createdAt;

  MarkDataDTO({required this.id, required this.questionId, required this.isCorrect, required this.createdAt, });

  factory MarkDataDTO.fromJson(Map<String, dynamic> json) => _$MarkDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$MarkDataDTOToJson(this);
}

@JsonSerializable()
class SendFriendRequestDTO {
  final int targetUserId;

  SendFriendRequestDTO({required this.targetUserId, });

  factory SendFriendRequestDTO.fromJson(Map<String, dynamic> json) => _$SendFriendRequestDTOFromJson(json);
  Map<String, dynamic> toJson() => _$SendFriendRequestDTOToJson(this);
}

@JsonSerializable()
class FriendRequestDataDTO {
  final int id;
  final int senderId;
  final int receiverId;
  final String status;
  final String createdAt;

  FriendRequestDataDTO({required this.id, required this.senderId, required this.receiverId, required this.status, required this.createdAt, });

  factory FriendRequestDataDTO.fromJson(Map<String, dynamic> json) => _$FriendRequestDataDTOFromJson(json);
  Map<String, dynamic> toJson() => _$FriendRequestDataDTOToJson(this);
}

@JsonSerializable()
class PublicUserProfileDTO {
  final int id;
  final String username;
  final String name;
  final String surname;

  PublicUserProfileDTO({required this.id, required this.username, required this.name, required this.surname, });

  factory PublicUserProfileDTO.fromJson(Map<String, dynamic> json) => _$PublicUserProfileDTOFromJson(json);
  Map<String, dynamic> toJson() => _$PublicUserProfileDTOToJson(this);
}

@JsonSerializable()
class ErrorDetailsDTO {
  final ErrorType type;
  final String? message;

  ErrorDetailsDTO({required this.type, this.message, });

  factory ErrorDetailsDTO.fromJson(Map<String, dynamic> json) => _$ErrorDetailsDTOFromJson(json);
  Map<String, dynamic> toJson() => _$ErrorDetailsDTOToJson(this);
}
