import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, lastValueFrom, take } from 'rxjs';
import { ProfileResponse } from './models';

const GET_PROFILE_URL =
  'https://amandaworkwork-production.up.railway.app/getProfileIdOpp';

const GET_PROFILE_BY_ID =
  'https://amandaworkwork-production.up.railway.app/getProfileById';

const GET_PROFILE_BY_SEARCH_URL =
  'https://amandaworkwork-production.up.railway.app/getProfileBySearchTerm';

const GET_PROFILE_BY_NAME =
  'https://amandaworkwork-production.up.railway.app/getProfileIdByName';
const GET_PROFILE_THAT_LIKED_USER =
  'https://amandaworkwork-production.up.railway.app/getProfileThatLikedUser';

@Injectable()
export class ProfileService {
  constructor(private httpClient: HttpClient) {}
  onGetAllProfileId = new Subject();
  onGetProfile = new Subject();
  onGetProfileThatLikedUser = new Subject();

  getProfileIds(cornToken: string, email: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams().set('email', email);
    let result$ = this.httpClient
      .get<ProfileResponse[]>(GET_PROFILE_URL, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: any) => {
      if (data == null) {
        this.onGetAllProfileId.next(null);
      } else {
        let result = data as ProfileResponse[];
        this.onGetAllProfileId.next(result);
        return data;
      }
    });
  }

  getProfileById(cornToken: string, profileId: string, useremail: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams()
      .set('profileId', profileId)
      .set('email', useremail);
    let result$ = this.httpClient
      .get<ProfileResponse>(GET_PROFILE_BY_ID, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: ProfileResponse) => {
      this.onGetProfile.next(data);
      return data;
    });
  }

  getProfileIdsBySearch(cornToken: string, email: string, searchTerm: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams()
      .set('email', email)
      .set('searchTerm', searchTerm);
    let result$ = this.httpClient
      .get<ProfileResponse[]>(GET_PROFILE_BY_SEARCH_URL, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: any) => {
      if (data == null) {
        this.onGetAllProfileId.next(null);
      } else {
        let result = data as ProfileResponse[];
        this.onGetAllProfileId.next(result);
        return data;
      }
    });
  }

  getProfileIdsByName(cornToken: string, email: string, name: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams()
      .set('email', email)
      .set('displayName', name);
    let result$ = this.httpClient
      .get<ProfileResponse[]>(GET_PROFILE_BY_NAME, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: any) => {
      if (data == null) {
        this.onGetAllProfileId.next(null);
      } else {
        let result = data as ProfileResponse[];
        this.onGetAllProfileId.next(result);
        return data;
      }
    });
  }

  getProfileThatLikesUser(
    cornToken: string,
    email: string,
    otherProfileId: string
  ) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams()
      .set('email', email)
      .set('otherProfileId', otherProfileId);

    let result$ = this.httpClient
      .get<ProfileResponse>(GET_PROFILE_THAT_LIKED_USER, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: ProfileResponse) => {
      this.onGetProfileThatLikedUser.next(data);
      return data;
    });
  }
}
