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

  me() {
    return this.http.get<User>(environment.user_url);
  }
  topup(amount: number) {
    return this.http.post<User>(environment.user_topup_url, amount);
  }
  changePassword(password: string) {
    return this.http.post<User>(environment.user_changepw_url, password);
  }
}
