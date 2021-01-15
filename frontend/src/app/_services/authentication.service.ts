import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { JWT } from '../_models/JWT';
import {User} from '../_models/User';

@Injectable()
export class AuthenticationService {
    constructor(private http: HttpClient) { }

    getRegisterToken() {
      const httpParams = new HttpParams()
        .set('grant_type', 'client_credentials');

      const httpOptions = {
        headers: new HttpHeaders({
          'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
          'Authorization': 'Basic ' + btoa('register-app:secret')
        })
      };

      return this.http.post<JWT>(environment.register_url, httpParams.toString(), httpOptions)
        .pipe(map((tok: JWT) => {
          // login successful if there's a jwt token in the response
          if (tok && tok.access_token) {
            console.log('GOT REGISTRATION token: ' + tok.access_token);
            localStorage.setItem('registerToken', tok.access_token);
          } else {
            console.log('error getting token');
            console.log(tok);
          }
        }));
    }

    register(token: string, username: string, password: string) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-type': 'application/json; charset=utf-8',
          'Authorization': 'Bearer ' + token
        })
      };
      // Submit registration using obtained token
      const user = new User;
      user.username = username;
      user.password = password;

      return this.http.post<User>(environment.api_url + '/register', user, httpOptions)
        .pipe(map( (res: User ) => {
          if (res && res.id) {
            console.log('Successfully registered with id: ' + res.id);
          } else {
            console.log('Failed to register.');
            console.log(res);
          }
        }));
    }

    isLogin() {
      return localStorage.getItem('currentUser') != null;
    }

    getLoginToken() {
      const cur = JSON.parse(localStorage.getItem('currentUser'));
      if (cur && cur.token) {
        return cur.token;
      }
      return null;
    }

    login(username: string, password: string) {
      const httpParams = new HttpParams()
      .set('grant_type', 'password')
      .set('username', username)
      .set('password', password);

      const httpOptions = {
        headers: new HttpHeaders({
          'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
          'Authorization': 'Basic ' + btoa('trusted-app:secret')
        })
      };

      return this.http.post<JWT>(environment.trusted_url, httpParams.toString(), httpOptions)
          .pipe(map((res: JWT) => {
              // login successful if there's a jwt token in the responsee
              if (res && res.access_token) {
                  console.log('GOT USER token: ' + res.access_token);
                  // store username and jwt token in local storage to keep user logged in between page refreshes
                  localStorage.setItem('currentUser', JSON.stringify({ username, token: res.access_token }));
                  // ToDO: Store refresh token and refresh at next request or change status to logged out
                  // when token expires
              } else {
                console.log('Error logging in');
                console.log(res);
              }
          }));
    }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('currentUser');
    }
}
