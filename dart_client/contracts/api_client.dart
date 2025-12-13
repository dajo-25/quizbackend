import 'package:dio/dio.dart';
import 'services/auth_service.dart';
import 'services/questions_service.dart';
import 'services/collections_service.dart';
import 'services/communities_service.dart';
import 'services/marks_service.dart';
import 'services/devices_service.dart';
import 'services/profile_service.dart';
export 'dtos.dart';
export 'enums.dart';
export 'services/auth_service.dart';
export 'services/questions_service.dart';
export 'services/collections_service.dart';
export 'services/communities_service.dart';
export 'services/marks_service.dart';
export 'services/devices_service.dart';
export 'services/profile_service.dart';

class ApiClient {
  final Dio dio;
  late final String baseUrl;

  late final AuthService authService;
  late final QuestionsService questionsService;
  late final CollectionsService collectionsService;
  late final CommunitiesService communitiesService;
  late final MarksService marksService;
  late final DevicesService devicesService;
  late final ProfileService profileService;

  ApiClient({String? baseUrl, Dio? dio}) : this.dio = dio ?? Dio() {
    this.baseUrl = baseUrl ?? '';
    this.dio.options.baseUrl = this.baseUrl;
    authService = AuthService(this.dio);
    questionsService = QuestionsService(this.dio);
    collectionsService = CollectionsService(this.dio);
    communitiesService = CommunitiesService(this.dio);
    marksService = MarksService(this.dio);
    devicesService = DevicesService(this.dio);
    profileService = ProfileService(this.dio);
  }
}
