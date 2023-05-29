import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, firstValueFrom, lastValueFrom, take } from 'rxjs';
import { ChatIdResponse, ChatMessage, GenericResponse } from './models';

const CHAT_ID_URL =
  'https://amandaworkwork-production.up.railway.app/getChatId';
const CREATE_MESSAGE_URL =
  'https://amandaworkwork-production.up.railway.app/saveMessage';
const GET_PAST_MESSAGES =
  'https://amandaworkwork-production.up.railway.app/getMessages';
@Injectable()
export class ChatService {
  constructor(private httpClient: HttpClient) {}
  onGetChatId = new Subject();
  onSendMessage = new Subject();
  onGetPastMessages = new Subject();

  getChatId(cornToken: string, email: string, otherProfileId: string) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);

    let queryParams = new HttpParams()
      .set('email', email)
      .set('otherProfileId', otherProfileId);

    let results$ = this.httpClient
      .get<ChatIdResponse>(CHAT_ID_URL, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return firstValueFrom(results$).then((data: ChatIdResponse) => {
      this.onGetChatId.next(data);
      return data;
    });
  }

  sendMessage(
    cornToken: string,
    chatId: number,
    message: string,
    email: string
  ) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded')
      .set('JWToken', cornToken);

    let queryParams = new HttpParams()
      .set('email', email)
      .set('chatId', chatId)
      .set('message', message);

    let result$ = this.httpClient
      .post<GenericResponse>(CREATE_MESSAGE_URL, queryParams.toString(), {
        headers: headers,
      })
      .pipe(take(1));

    return lastValueFrom(result$).then((data: GenericResponse) => {
      this.onSendMessage.next(data);
      return data;
    });
  }

  getPastMessages(cornToken: string, chatId: number) {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('JWToken', cornToken);
    let queryParams = new HttpParams().set('chatId', chatId);
    let result$ = this.httpClient
      .get<ChatMessage[]>(GET_PAST_MESSAGES, {
        params: queryParams,
        headers: headers,
      })
      .pipe(take(1));
    return lastValueFrom(result$).then((data: any) => {
      if (data == null || data == undefined) {
        this.onGetPastMessages.next(null);
        return data;
      } else {
        let result = data as ChatMessage[];
        this.onGetPastMessages.next(result);
        return data;
      }
    });
  }
}
