// ToDo: Authenticate against REST service, using a dedicated "login" component, maybe?
//      Provide user information to user.component, which will display it
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { User } from '../_models/User';
import {Observable} from 'rxjs';

@Injectable()
export class UserService {
  constructor(private http: HttpClient) {}
  getAll() {
    return this.http.get<User[]>(environment.api_url + '/users');
  }
  me() {
    return this.http.get<User>(environment.api_url + '/me');
  }
  positions() {
    return this.http.get<Position[]>(environment.api_url + '/positions');
  }
}
