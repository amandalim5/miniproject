import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TokenService } from '../token.service';
import { GenericResponse, TokenResponse } from '../models';
import { UserService } from '../user.service';

@Component({
  selector: 'app-deleteaccount',
  templateUrl: './deleteaccount.component.html',
  styleUrls: ['./deleteaccount.component.css'],
})
export class DeleteaccountComponent implements OnInit, OnDestroy {
  cornToken!: string;
  checkToken!: Subscription;
  emailAddress: string = '';
  currentemail!: string;
  deleteUserSub!: Subscription;
  constructor(
    private router: Router,
    private tokenSvc: TokenService,
    private userSvc: UserService
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
            this.currentemail = response.useremail;
            sessionStorage.removeItem('cornToken');
            sessionStorage.setItem('cornToken', response.newToken);
            this.checkToken.unsubscribe();
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

  deleteAccount() {
    this.deleteUserSub = this.userSvc.onDeleteUser.subscribe({
      next: (res: any) => {
        let response = res as GenericResponse;
        if (response.result == true) {
          alert('Your account has been deleted. Bye bye!');
          sessionStorage.removeItem('cornToken');
          this.router.navigate(['/login']);
        } else {
          alert('Your account was not deleted. Please try again.');
        }
        this.deleteUserSub.unsubscribe();
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    this.userSvc.deleteUser(token, this.currentemail);
  }
}
