import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class MarksService {
  final Dio _dio;
  MarksService(this._dio);

  Future<DTOResponse<MarkListResponseDTO>> getMarks({String? bearerToken}) async {
    final response = await _dio.get('/marks', options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<MarkListResponseDTO>.fromJson(response.data, (json) => MarkListResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
