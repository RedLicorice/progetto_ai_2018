import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../_services/authentication.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(private auth: AuthenticationService) {}
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // add authorization header with jwt token if available
        if (!request.headers.has('Authorization')) {
          const curToken = this.auth.getLoginToken();
          if (curToken) {
            // console.log('Appending authenticated user\'s token!');
            request = request.clone({
              setHeaders: {
                Authorization: `Bearer ${curToken}`
              }
            });
          }
        }
        return next.handle(request);
    }
}
