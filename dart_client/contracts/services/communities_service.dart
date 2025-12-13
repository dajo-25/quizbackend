import 'package:dio/dio.dart';
import '../dtos.dart';
import '../enums.dart';

class CommunitiesService {
  final Dio _dio;
  CommunitiesService(this._dio);

  Future<DTOResponse<FriendRequestResponseDTO>> postFriendRequest({required SendFriendRequestDTO body, String? bearerToken}) async {
    final response = await _dio.post('/communities/friend-request', data: body.toJson(), options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<FriendRequestResponseDTO>.fromJson(response.data, (json) => FriendRequestResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<GenericResponseDTO>> postRespond({required RespondFriendRequestRequestDTO body, String? bearerToken}) async {
    final response = await _dio.post('/communities/friend-request/respond', data: body.toJson(), options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<GenericResponseDTO>.fromJson(response.data, (json) => GenericResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<FriendRequestListResponseDTO>> getFriendRequests({String? bearerToken}) async {
    final response = await _dio.get('/communities/friend-requests', options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<FriendRequestListResponseDTO>.fromJson(response.data, (json) => FriendRequestListResponseDTO.fromJson(json as Map<String, dynamic>));
  }

  Future<DTOResponse<UserListResponseDTO>> getUsers({String? bearerToken}) async {
    final response = await _dio.get('/communities/users', options: bearerToken != null ? Options(headers: {'Authorization': 'Bearer $bearerToken'}) : null);
    return DTOResponse<UserListResponseDTO>.fromJson(response.data, (json) => UserListResponseDTO.fromJson(json as Map<String, dynamic>));
  }

}
