import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, lastValueFrom, take } from 'rxjs';
import { GenericResponse, ProfileResponse } from './models';

const LIKE_A_PROFILE =
  'https://amandaworkwork-production.up.railway.app/likeAPerson';

const GET_LIST_OF_LIKES =
  'https://amandaworkwork-production.up.railway.app/getProfilesWhoLikeUser';
const LIKE_A_PROFILE_BACK =
  'https://amandaworkwork-production.up.railway.app/likeAPersonBack';
@Injectable()
export class LikeService {
  constructor(private httpClient: HttpClient) {}
  onLike = new Subject();
  onGetListOfLikes = new Subject();
  onLikeBack = new Subject();

  likeAProfile(cornToken: string, email: string, otherprofileId: number) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams()
      .set('email', email)
      .set('otherprofileId', otherprofileId);
    let result$ = this.httpClient
      .get<GenericResponse>(LIKE_A_PROFILE, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: GenericResponse) => {
      this.onLike.next(data);
      return data;
    });
  }

  getListOfLikes(cornToken: string, email: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams().set('email', email);
    let result$ = this.httpClient
      .get<ProfileResponse[]>(GET_LIST_OF_LIKES, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: any) => {
      if (data == null) {
        this.onGetListOfLikes.next(null);
      } else {
        let result = data as ProfileResponse[];
        this.onGetListOfLikes.next(result);
        return data;
      }
    });
  }

  likeAProfileBack(cornToken: string, email: string, otherprofileId: number) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams()
      .set('email', email)
      .set('otherprofileId', otherprofileId);
    let result$ = this.httpClient
      .get<GenericResponse>(LIKE_A_PROFILE_BACK, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: GenericResponse) => {
      this.onLikeBack.next(data);
      return data;
    });
  }
}
