import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, lastValueFrom, take } from 'rxjs';
import { ResetResponse, TokenResponse } from './models';

const TOKEN_URL = 'https://amandaworkwork-production.up.railway.app/token';
const RESET_URL =
  'https://amandaworkwork-production.up.railway.app/sendResetToken';
const VERIFY_URL =
  'https://amandaworkwork-production.up.railway.app/changePassword';

@Injectable()
export class TokenService {
  constructor(private httpClient: HttpClient) {}
  onCheck = new Subject();
  onUser = new Subject();
  onToken = new Subject();
  onReset = new Subject();
  onVerify = new Subject();

  checkToken(token: string) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    let queryParams = new HttpParams().set('cornToken', token);
    let result$ = this.httpClient
      .get<TokenResponse>(TOKEN_URL, { params: queryParams, headers: headers })
      .pipe(take(1));

    return lastValueFrom(result$).then((data: TokenResponse) => {
      this.onCheck.next(data);
      return data;
      // console.info(data.newToken.toString() + ' from oncheck in token service');
      // this.onCheck.next(data.result);
      // if (data.result) {
      //   this.onUser.next(data.useremail.toString());
      //   this.onToken.next(data.newToken.toString());
      // }

      // return data;
    });
  }

  sendToken(email: string) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    let queryParams = new HttpParams().set('email', email);
    let result$ = this.httpClient
      .get<ResetResponse>(RESET_URL, { params: queryParams, headers: headers })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: ResetResponse) => {
      console.info(
        data.result + data.message + ' this was from the amandaworkwork'
      );
      this.onReset.next(data.result);
      return data;
    });
  }

  verifyPasswordToken(email: string, token: string, password: string) {
    const headers = new HttpHeaders().set(
      'Content-Type',
      'application/x-www-form-urlencoded'
    );
    const verify = new HttpParams()
      .set('email', email)
      .set('token', token)
      .set('password', password);

    let result$ = this.httpClient
      .post<ResetResponse>(VERIFY_URL, verify.toString(), { headers: headers })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: ResetResponse) => {
      console.info('this is the result from changing password: ' + data.result);
      this.onVerify.next(data.result);
      return data;
    });
  }
}
