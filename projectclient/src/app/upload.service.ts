import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, lastValueFrom, take } from 'rxjs';
import { GenericResponse, UploadResult } from './models';
const UPLOAD_URL = 'https://amandaworkwork-production.up.railway.app/upload';

const DELETE_URL = 'https://amandaworkwork-production.up.railway.app/deletepic';

@Injectable()
export class UploadService {
  constructor(private httpClient: HttpClient) {}

  onUploadPic = new Subject();
  onDeletePic = new Subject();

  uploadPicture(cornToken: string, email: string, picture: Blob) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded')
      .set('JWToken', cornToken);

    const formdata = new FormData();
    formdata.set('email', email);
    formdata.set('file', picture);

    return lastValueFrom(
      this.httpClient.post<UploadResult>(UPLOAD_URL, formdata)
    );
  }

  deletePicture(cornToken: string, email: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded')
      .set('JWToken', cornToken);

    const userDetails = new HttpParams().set('email', email);

    let result$ = this.httpClient
      .put<GenericResponse>(DELETE_URL, userDetails.toString(), {
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((res: GenericResponse) => {
      this.onDeletePic.next(res);
      return res;
    });
  }
}
