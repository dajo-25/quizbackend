import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class QuestionsService {
  final Dio _dio;
  QuestionsService(this._dio);

  Future<DTOResponse<QuestionListResponseDTO>> getQuestions({required int page, String? locale}) async {
    final response = await _dio.get('/questions', queryParameters: {'page': page, 'locale': locale, });
    return DTOResponse<QuestionListResponseDTO>.fromJson(response.data, (json) => QuestionListResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> postQuestions({required CreateQuestionsRequestDTO body}) async {
    final response = await _dio.post('/questions', data: body.toJson());
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<QuestionDataResponseDTO>> getQuestionsId({required int id, String? locale}) async {
    final response = await _dio.get('/questions/$id', queryParameters: {'locale': locale, });
    return DTOResponse<QuestionDataResponseDTO>.fromJson(response.data, (json) => QuestionDataResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<QuestionDataResponseDTO>> putQuestionsId({required UpdateQuestionRequestDTO body, required int id}) async {
    final response = await _dio.put('/questions/$id', data: body.toJson());
    return DTOResponse<QuestionDataResponseDTO>.fromJson(response.data, (json) => QuestionDataResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> deleteQuestionsId({required int id}) async {
    final response = await _dio.delete('/questions/$id');
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<QuestionListResponseDTO>> getBatch({required List<int> ids, String? locale}) async {
    final response = await _dio.get('/questions/batch', queryParameters: {'ids': ids, 'locale': locale, });
    return DTOResponse<QuestionListResponseDTO>.fromJson(response.data, (json) => QuestionListResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
