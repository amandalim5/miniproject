import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, firstValueFrom, take } from 'rxjs';
import { Login, LoginResponse } from './models';

const LOGIN_URL = 'https://amandaworkwork-production.up.railway.app/login';

@Injectable()
export class LoginService {
  constructor(private httpClient: HttpClient) {}
  onLogin = new Subject();
  onToken = new Subject();

  login(login: Login) {
    const headers = new HttpHeaders().set(
      'Content-Type',
      'application/x-www-form-urlencoded'
    );

    const loggingIn = new HttpParams()
      .set('email', login.email)
      .set('password', login.password);

    let result$ = this.httpClient
      .post<LoginResponse>(LOGIN_URL, loggingIn.toString(), {
        headers: headers,
      })
      .pipe(take(1));

    return firstValueFrom(result$).then((data: LoginResponse) => {
      this.onLogin.next(data);
      return data;
    });
  }
}
