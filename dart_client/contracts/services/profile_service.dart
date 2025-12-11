import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class ProfileService {
  final Dio _dio;
  ProfileService(this._dio);

  Future<DTOResponse<ProfileDataResponseDTO>> getProfile({String? bearerToken}) async {
    final response = await _dio.get('/profile', options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<ProfileDataResponseDTO>.fromJson(response.data, (json) => ProfileDataResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<ProfileDataResponseDTO>> putProfile({required UpdateProfileRequestDTO body, String? bearerToken}) async {
    final response = await _dio.put('/profile', data: body.toJson(), options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<ProfileDataResponseDTO>.fromJson(response.data, (json) => ProfileDataResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
