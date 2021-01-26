import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { JWT } from '../_models/JWT';
import {User} from '../_models/User';
import * as moment from 'moment';

@Injectable()
export class AuthenticationService {
  constructor(private http: HttpClient) { }
  /*
  * Service-action methods, ie methods directly providing service behavior
  * such as register, login and logout.
  * */
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
                localStorage.setItem('currentUser', JSON.stringify({
                  username: username,
                  token: res.access_token,
                  expires_in: res.expires_in,
                  refresh_token: res.refresh_token,
                  expiry: moment().add(res.expires_in, 'seconds') // When will this token be not valid anymore?
                }));
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

  refresh() {
    // The flow for refresh token  supposes the use of app's client credentials for endpoint authentications (Basic auth)
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
        'Authorization': 'Basic ' + btoa(environment.client_credentials)
      })
    };
    // In order to get a new access token, we send a form-data request with 'refresh_token' grant type
    // and the refresh token as body
    const httpParams = new HttpParams()
      .set('grant_type', 'refresh_token')
      .set('refresh_token', this.getRefreshToken());

    return this.http.post<JWT>(environment.trusted_url, httpParams.toString(), httpOptions)
      .pipe(map((res: JWT) => {
        // login successful if there's a jwt token in the responsee
        if (res && res.access_token) {
          console.log('GOT USER token: ' + res.access_token);
          // store username and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify({
            username: this.getLoginUsername(),
            token: res.access_token,
            expires_in: res.expires_in,
            refresh_token: res.refresh_token,
            // Used to set a timer in app.component.ts for token refresh
            expiry: moment().add(res.expires_in, 'seconds') // When will this token be not valid anymore?
          }));
        } else {
          console.log('Error logging in');
          console.log(res);
        }
      }));
  }

  checkLogin(): void {
    this.http.get<User>(environment.user_url, {}).subscribe(
      (res) => {
        // User is logged in, no issues
        console.log('login is valid', res);
      },
      (err) => {
        // If we get 401 error, login is not valid anymore: logout the user.
        console.log('login is invalid', err);
        if ( err.status === 401) {
          this.logout();
        }
      }
    );
  }

  /*
  * Utility methods (for getting logged-in user parameters)
  * */
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

  getRefreshToken() {
    const cur = JSON.parse(localStorage.getItem('currentUser'));
    if (cur && cur.token) {
      return cur.refresh_token;
    }
    return null;
  }

  getLoginExpiry() {
    const cur = JSON.parse(localStorage.getItem('currentUser'));
    if (cur && cur.expiry) {
      return cur.expiry;
    }
    return null;
  }

  getLoginUsername() {
    const cur = JSON.parse(localStorage.getItem('currentUser'));
    if (cur && cur.username) {
      return cur.username;
    }
    return null;
  }

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
}
