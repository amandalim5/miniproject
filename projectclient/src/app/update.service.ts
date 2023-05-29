import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Subject, lastValueFrom, take } from 'rxjs';
import { Injectable } from '@angular/core';
import { GenericResponse, Profile, ProfileResponse } from './models';
const UPDATE_PASSWORD_URL =
  'https://amandaworkwork-production.up.railway.app/updatePassword';

const GET_PROFILE_URL =
  'https://amandaworkwork-production.up.railway.app/getProfile';

const UPDATE_PROFILE_URL =
  'https://amandaworkwork-production.up.railway.app/updateProfile';

@Injectable()
export class UpdateService {
  constructor(private httpClient: HttpClient) {}

  onUpdatePassword = new Subject();
  onGetProfile = new Subject();
  onUpdateProfile = new Subject();

  updatePassword(cornToken: string, newPassword: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded')
      // .set('Access-Control-Allow-Origin', '*')
      .set('JWToken', cornToken);
    // .set('Access-Control-Expose-Headers', 'Content-Length')
    // .set('Access-Control-Allow-Headers', 'Authorization');

    const passwordDetail = new HttpParams()
      .set('newpassword', newPassword)
      .set('token', cornToken);

    let result$ = this.httpClient
      .put<GenericResponse>(UPDATE_PASSWORD_URL, passwordDetail.toString(), {
        headers: headers,
      })
      .pipe(take(1));

    return lastValueFrom(result$).then((res: GenericResponse) => {
      this.onUpdatePassword.next(res);
      return res;
    });
  }

  getProfile(cornToken: string, email: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams().set('email', email);
    let result$ = this.httpClient
      .get<ProfileResponse>(GET_PROFILE_URL, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: ProfileResponse) => {
      this.onGetProfile.next(data);
      return data;
    });
  }

  updateProfile(cornToken: string, profile: Profile) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded')
      .set('JWToken', cornToken);

    const profileupdate = new HttpParams()
      .set('email', profile.email)
      .set('profileIsPublic', profile.profileIsPublic)
      .set('displayName', profile.displayName)
      .set('summary', profile.summary)
      .set('birthday', profile.birthday)
      .set('birthmonth', profile.birthmonth)
      .set('birthyear', profile.birthyear)
      .set('height', profile.height)
      .set('weight', profile.weight)
      .set('isSmoking', profile.isSmoking)
      .set('postalCode', profile.postalCode)
      .set('mail', profile.mail);

    let result$ = this.httpClient
      .post<GenericResponse>(UPDATE_PROFILE_URL, profileupdate.toString(), {
        headers: headers,
      })
      .pipe(take(1));

    return lastValueFrom(result$).then((data: GenericResponse) => {
      this.onUpdateProfile.next(data);
      return data;
    });
  }
}
