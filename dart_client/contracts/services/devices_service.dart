import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class DevicesService {
  final Dio _dio;
  DevicesService(this._dio);

  Future<DTOResponse<GenericResponseDTO>> postPushToken({required RegisterPushTokenRequestDTO body, String? bearerToken}) async {
    final response = await _dio.post('/devices/push-token', data: body.toJson(), options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> deletePushToken({String? bearerToken}) async {
    final response = await _dio.delete('/devices/push-token', options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
