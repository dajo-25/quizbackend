import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class AuthService {
  final Dio _dio;
  AuthService(this._dio);

  Future<DTOResponse<LoginResponseDTO>> postLogin({required LoginRequestDTO body}) async {
    final response = await _dio.post('/auth/login', data: body.toJson());
    return DTOResponse<LoginResponseDTO>.fromJson(response.data, (json) => LoginResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<LoginResponseDTO>> postSignup({required SignupRequestDTO body}) async {
    final response = await _dio.post('/auth/signup', data: body.toJson());
    return DTOResponse<LoginResponseDTO>.fromJson(response.data, (json) => LoginResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> postLogout({}) async {
    final response = await _dio.post('/auth/logout');
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<MessageResponseDTO>> deleteAccount({}) async {
    final response = await _dio.delete('/auth/account');
    return DTOResponse<MessageResponseDTO>.fromJson(response.data, (json) => MessageResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<MessageResponseDTO>> postRecover({required RecoverPasswordRequestDTO body}) async {
    final response = await _dio.post('/auth/recover', data: body.toJson());
    return DTOResponse<MessageResponseDTO>.fromJson(response.data, (json) => MessageResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> postVerify({required VerifyEmailRequestDTO body}) async {
    final response = await _dio.post('/auth/verify', data: body.toJson());
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<MustChangePasswordResponseDTO>> getMustChangePassword({}) async {
    final response = await _dio.get('/auth/must-change-password');
    return DTOResponse<MustChangePasswordResponseDTO>.fromJson(response.data, (json) => MustChangePasswordResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> postChangePassword({required ChangePasswordRequestDTO body}) async {
    final response = await _dio.post('/auth/change-password', data: body.toJson());
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<UserStatusResponseDTO>> getStatus({}) async {
    final response = await _dio.get('/auth/status');
    return DTOResponse<UserStatusResponseDTO>.fromJson(response.data, (json) => UserStatusResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
