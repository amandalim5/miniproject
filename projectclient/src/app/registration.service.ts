import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, lastValueFrom, take } from 'rxjs';
import { GenericResponse, Registration } from './models';

const REGISTRATION_API =
  'https://amandaworkwork-production.up.railway.app/register';

@Injectable()
export class RegistrationService {
  constructor(private httpClient: HttpClient) {}
  onRegister = new Subject();

  register(register: Registration) {
    const headers = new HttpHeaders().set(
      'Content-Type',
      'application/x-www-form-urlencoded'
    );
    const registration = new HttpParams()
      .set('userName', register.userName)
      .set('userEmail', register.userEmail)
      .set('userPassword', register.userPassword)
      .set('gender', register.gender);
    let result$ = this.httpClient
      .post<GenericResponse>(REGISTRATION_API, registration.toString(), {
        headers: headers,
      })
      .pipe(take(1));

    return lastValueFrom(result$).then((data: GenericResponse) => {
      this.onRegister.next(data);
      return data;
    });
  }
}
