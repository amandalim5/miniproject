import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, firstValueFrom, take } from 'rxjs';
import { DistanceResponse } from './models';

const DISTANCE_URL =
  'https://amandaworkwork-production.up.railway.app/getDistance';
const DISTANCE_EMAIL_URL =
  'https://amandaworkwork-production.up.railway.app/getDistanceUsingEmail';
@Injectable()
export class DistanceService {
  constructor(private httpClient: HttpClient) {}
  onDistance = new Subject();

  getDistance(
    cornToken: string,
    userprofileId: number,
    otherProfileId: number
  ) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);

    let queryParams = new HttpParams()
      .set('userprofileId', userprofileId)
      .set('otherprofileId', otherProfileId);

    let results$ = this.httpClient
      .get<DistanceResponse>(DISTANCE_URL, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return firstValueFrom(results$).then((data: DistanceResponse) => {
      this.onDistance.next(data);
      return data;
    });
  }

  getDistanceWithEmail(
    cornToken: string,
    userEmail: string,
    otherProfileId: number
  ) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);

    let queryParams = new HttpParams()
      .set('email', userEmail)
      .set('otherprofileId', otherProfileId);

    let results$ = this.httpClient
      .get<DistanceResponse>(DISTANCE_EMAIL_URL, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return firstValueFrom(results$).then((data: DistanceResponse) => {
      this.onDistance.next(data);
      return data;
    });
  }
}
