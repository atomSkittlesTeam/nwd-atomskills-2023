import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthHelperInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (localStorage.getItem("AUTH")) {
      request = request.clone({
        setHeaders: {
          Authorization: `Basic ${localStorage.getItem("AUTH")}`
        }
      });
    }
    return next.handle(request);
  }
}
