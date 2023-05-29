import {
  Component,
  OnInit,
  ElementRef,
  ViewChild,
  AfterViewChecked,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TokenService } from '../token.service';
import {
  ChatIdResponse,
  ChatMessage,
  GenericResponse,
  TokenResponse,
} from '../models';
import { ChatService } from '../chat.service';

@Component({
  selector: 'app-matchedlist',
  templateUrl: './matchedlist.component.html',
  styleUrls: ['./matchedlist.component.css'],
})
export class MatchedlistComponent implements OnInit, AfterViewChecked {
  // this is for the chat function
  cornToken!: string;
  checkToken!: Subscription;
  emailAddress: string = '';
  currentemail!: string;
  chatIdSub!: Subscription;
  currentChatId: number = 0;
  currentProfileId: number = 0;
  currentChat = {} as ChatIdResponse;
  newMessage: string = '';
  sendMessageSub!: Subscription;
  getMessagesAgain!: Subscription;
  getPastMessagesSub!: Subscription;
  pastMessages!: ChatMessage[];
  theInterval: any;
  // intervalId = setInterval(this.getMessages, 5000);

  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;
  constructor(
    private router: Router,
    private tokenSvc: TokenService,
    private chatSvc: ChatService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.currentProfileId = this.activatedRoute.snapshot.params['profileId'];
    let corntoken = sessionStorage.getItem('cornToken');
    if (corntoken == null) {
      this.router.navigate(['/login']);
    } else {
      this.cornToken = corntoken;
      this.checkToken = this.tokenSvc.onCheck.subscribe({
        next: (result: any) => {
          let response = result as TokenResponse;
          if (response.result == true) {
            this.currentemail = response.useremail;
            sessionStorage.removeItem('cornToken');
            sessionStorage.setItem('cornToken', response.newToken);
            this.checkToken.unsubscribe();
            this.chatIdSub = this.chatSvc.onGetChatId.subscribe({
              next: (res: any) => {
                let response = res as ChatIdResponse;
                if (response.result == true) {
                  this.currentChat = response;
                  this.currentChatId = response.chatId;
                  if (response.photoUrl == '') {
                    this.currentChat.photoUrl =
                      'https://cdn-icons-png.flaticon.com/512/877/877698.png';
                  }
                  console.info(
                    '==============> checking for the chatId: ' +
                      this.currentChatId +
                      response.otherDisplayName +
                      this.currentChat.photoUrl
                  );
                  this.chatIdSub.unsubscribe();
                  this.getPastMessagesSub =
                    this.chatSvc.onGetPastMessages.subscribe({
                      next: (result: any) => {
                        this.pastMessages = result as ChatMessage[];
                        this.getPastMessagesSub.unsubscribe();
                      },
                    });
                  let token = sessionStorage.getItem('cornToken');
                  if (token == null) {
                    token = '';
                  }
                  this.chatSvc.getPastMessages(token, this.currentChatId);
                } else {
                  this.chatIdSub.unsubscribe();
                  this.router.navigate(['/home']);
                }
              },
            });
            let token = sessionStorage.getItem('cornToken');
            if (token == null) {
              token = '';
            }
            this.chatSvc.getChatId(
              token,
              this.currentemail,
              this.currentProfileId.toString()
            );
          } else {
            sessionStorage.removeItem('cornToken');
            this.checkToken.unsubscribe();
            alert('Your session has expired. Bringing you to the login page!');
            this.router.navigate(['/login']);
          }
        },
      });

      this.tokenSvc.checkToken(corntoken);
      this.scrollToBottom();
    }
    this.theInterval = setInterval(() => {
      console.info(this.currentemail + ' what is the issue....');
      console.info('Getting the messages!!!!');
      this.getMessagesAgain = this.chatSvc.onGetPastMessages.subscribe({
        next: (result: any) => {
          this.pastMessages = result as ChatMessage[];
          this.getMessagesAgain.unsubscribe();
        },
      });
      let token = sessionStorage.getItem('cornToken');
      if (token == null) {
        token = '';
      }
      console.info(token);
      console.info(this.currentChatId.toString() + '....');
      this.chatSvc.getPastMessages(token, this.currentChatId);
    }, 5 * 1000);
  }
  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop =
        this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) {}
  }
  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  sendMessage() {
    this.sendMessageSub = this.chatSvc.onSendMessage.subscribe({
      next: (res: any) => {
        let response = res as GenericResponse;
        this.sendMessageSub.unsubscribe();
        if (response.result == true) {
          sessionStorage.removeItem('cornToken');
          sessionStorage.setItem('cornToken', response.message);
          this.getPastMessagesSub = this.chatSvc.onGetPastMessages.subscribe({
            next: (result: any) => {
              this.pastMessages = result as ChatMessage[];
              this.getPastMessagesSub.unsubscribe();
            },
          });
          let token = sessionStorage.getItem('cornToken');
          if (token == null) {
            token = '';
          }
          this.chatSvc.getPastMessages(token, this.currentChatId);
        }
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    console.info(
      'Checking for the value of the token in matchedlist ts: ' + token
    );
    this.chatSvc.sendMessage(
      token,
      this.currentChatId,
      this.newMessage,
      this.currentemail
    );
    this.newMessage = '';
  }

  ngOnDestroy() {
    if (this.theInterval) {
      clearInterval(this.theInterval);
    }
    // clearInterval(this.intervalId);
  }

  // getMessages() {
  //   console.info(this.currentemail + ' what is the issue....');
  //   console.info('Getting the messages!!!!');
  //   // this.getMessagesAgain = this.chatSvc.onGetPastMessages.subscribe({
  //   //   next: (result: any) => {
  //   //     this.pastMessages = result as ChatMessage[];
  //   //     this.getMessagesAgain.unsubscribe();
  //   //   },
  //   // });
  //   let token = sessionStorage.getItem('cornToken');
  //   if (token == null) {
  //     token = '';
  //   }
  //   console.info(token);
  //   console.info(this.currentChatId.toString() + '....');
  //   this.chatSvc.getPastMessages(token, this.currentChatId);
  // }
}
