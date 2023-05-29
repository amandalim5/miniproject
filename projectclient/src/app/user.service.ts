import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, lastValueFrom, take } from 'rxjs';
import { GenericResponse, UserResponse } from './models';
const GET_USER =
  'https://amandaworkwork-production.up.railway.app/getUserDetails';

const DELETE_USER =
  'https://amandaworkwork-production.up.railway.app/deleteAccount';

@Injectable()
export class UserService {
  constructor(private httpClient: HttpClient) {}

  onGetUser = new Subject();
  onDeleteUser = new Subject();

  getUser(cornToken: string, email: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams().set('email', email);
    let result$ = this.httpClient
      .get<UserResponse>(GET_USER, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: UserResponse) => {
      this.onGetUser.next(data);
      return data;
    });
  }

  deleteUser(cornToken: string, email: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams().set('email', email);
    let result$ = this.httpClient
      .delete<GenericResponse>(DELETE_USER, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: GenericResponse) => {
      this.onDeleteUser.next(data);
      return data;
    });
  }
}
