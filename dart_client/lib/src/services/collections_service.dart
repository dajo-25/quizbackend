import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class CollectionsService {
  final Dio _dio;
  CollectionsService(this._dio);

  Future<DTOResponse<CollectionListResponseDTO>> getCollections({}) async {
    final response = await _dio.get('/collections');
    return DTOResponse<CollectionListResponseDTO>.fromJson(response.data, (json) => CollectionListResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<IdDataResponseDTO>> postCollections({required CreateCollectionRequestDTO body}) async {
    final response = await _dio.post('/collections', data: body.toJson());
    return DTOResponse<IdDataResponseDTO>.fromJson(response.data, (json) => IdDataResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<CollectionDetailResponseDTO>> getCollectionsId({required int id}) async {
    final response = await _dio.get('/collections/$id');
    return DTOResponse<CollectionDetailResponseDTO>.fromJson(response.data, (json) => CollectionDetailResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> putCollectionsId({required UpdateCollectionRequestDTO body, required int id}) async {
    final response = await _dio.put('/collections/$id', data: body.toJson());
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> deleteCollectionsId({required int id}) async {
    final response = await _dio.delete('/collections/$id');
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
