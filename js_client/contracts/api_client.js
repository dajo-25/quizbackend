import { AuthService } from './services/auth_service.js';
import { QuestionsService } from './services/questions_service.js';
import { CollectionsService } from './services/collections_service.js';
import { CommunitiesService } from './services/communities_service.js';
import { MarksService } from './services/marks_service.js';
import { DevicesService } from './services/devices_service.js';
import { ProfileService } from './services/profile_service.js';

export class ApiClient {
  constructor({ baseUrl, fetcher } = {}) {
    this.baseUrl = baseUrl || '';
    this.fetcher = fetcher || fetch;
    this.authService = new AuthService(this.baseUrl, this.fetcher);
    this.questionsService = new QuestionsService(this.baseUrl, this.fetcher);
    this.collectionsService = new CollectionsService(this.baseUrl, this.fetcher);
    this.communitiesService = new CommunitiesService(this.baseUrl, this.fetcher);
    this.marksService = new MarksService(this.baseUrl, this.fetcher);
    this.devicesService = new DevicesService(this.baseUrl, this.fetcher);
    this.profileService = new ProfileService(this.baseUrl, this.fetcher);
  }
}
