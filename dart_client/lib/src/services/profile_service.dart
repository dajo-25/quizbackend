import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class ProfileService {
  final Dio _dio;
  ProfileService(this._dio);

  Future<DTOResponse<ProfileDataResponseDTO>> getProfile({}) async {
    final response = await _dio.get('/profile');
    return DTOResponse<ProfileDataResponseDTO>.fromJson(response.data, (json) => ProfileDataResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<ProfileDataResponseDTO>> putProfile({required UpdateProfileRequestDTO body}) async {
    final response = await _dio.put('/profile', data: body.toJson());
    return DTOResponse<ProfileDataResponseDTO>.fromJson(response.data, (json) => ProfileDataResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
