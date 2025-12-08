import 'package:dio/dio.dart';
import 'src/services/auth_service.dart';
import 'src/services/questions_service.dart';
import 'src/services/collections_service.dart';
import 'src/services/communities_service.dart';
import 'src/services/marks_service.dart';
import 'src/services/devices_service.dart';
import 'src/services/profile_service.dart';
export 'src/dtos.dart';
export 'src/enums.dart';
export 'src/services/auth_service.dart';
export 'src/services/questions_service.dart';
export 'src/services/collections_service.dart';
export 'src/services/communities_service.dart';
export 'src/services/marks_service.dart';
export 'src/services/devices_service.dart';
export 'src/services/profile_service.dart';

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
