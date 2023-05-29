import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { TokenService } from '../token.service';
import { ProfileResponse, TokenResponse } from '../models';
import { LikeService } from '../like.service';

@Component({
  selector: 'app-listoflikes',
  templateUrl: './listoflikes.component.html',
  styleUrls: ['./listoflikes.component.css'],
})
export class ListoflikesComponent implements OnInit, OnDestroy {
  cornToken!: string;
  checkToken!: Subscription;
  getListSub!: Subscription;
  listOfProfiles!: ProfileResponse[];
  currentemail!: string;

  constructor(
    private router: Router,
    private tokenSvc: TokenService,
    private likeSvc: LikeService
  ) {}

  ngOnInit(): void {
    let corntoken = sessionStorage.getItem('cornToken');
    if (corntoken == null) {
      this.router.navigate(['/login']);
    } else {
      this.cornToken = corntoken;
      this.checkToken = this.tokenSvc.onCheck.subscribe({
        next: (result: any) => {
          let response = result as TokenResponse;
          if (response.result == true) {
            sessionStorage.removeItem('cornToken');
            this.currentemail = response.useremail;
            sessionStorage.setItem('cornToken', response.newToken);
            this.checkToken.unsubscribe();
            this.getListSub = this.likeSvc.onGetListOfLikes.subscribe({
              next: (res: any) => {
                if (res == null) {
                  alert('No one has liked you yet!');
                } else {
                  this.listOfProfiles = res as ProfileResponse[];
                  for (var p of this.listOfProfiles) {
                    if (p.photo == '') {
                      p.photoUrl =
                        'https://cdn-icons-png.flaticon.com/512/877/877698.png';
                    }
                  }
                }
                this.getListSub.unsubscribe();
              },
            });
            let token = sessionStorage.getItem('cornToken');
            if (token == null) {
              token = '';
            }
            this.likeSvc.getListOfLikes(token, this.currentemail);
          } else {
            sessionStorage.removeItem('cornToken');
            this.checkToken.unsubscribe();
            alert('Your session has expired. Bringing you to the login page!');
            this.router.navigate(['/login']);
          }
        },
      });

      this.tokenSvc.checkToken(corntoken);
    }
  }

  ngOnDestroy(): void {
    if (this.checkToken) this.checkToken.unsubscribe();
  }
}
