import { Component, OnInit, OnDestroy, DoCheck } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { LoginService } from '../login.service';
import { Login, LoginResponse, TokenResponse } from '../models';
import { TokenService } from '../token.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
  hide = true;
  cornToken!: string;
  passwordiswrong = false;
  loginForm!: FormGroup;
  subscription!: Subscription;

  checkToken!: Subscription;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private loginSvc: LoginService,
    private tokenSvc: TokenService
  ) {}
  ngOnInit(): void {
    let corntoken = sessionStorage.getItem('cornToken');
    if (corntoken != null) {
      this.cornToken = corntoken;
      this.checkToken = this.tokenSvc.onCheck.subscribe({
        next: (result: any) => {
          let response = result as TokenResponse;
          if (response.result == true) {
            sessionStorage.removeItem('cornToken');
            sessionStorage.setItem('cornToken', response.newToken);
            this.router.navigate(['/home']);
            this.checkToken.unsubscribe();
          } else {
            sessionStorage.removeItem('cornToken');
            this.checkToken.unsubscribe();
          }
        },
      });
      this.tokenSvc.checkToken(corntoken);
    }

    this.loginForm = this.fb.group({
      email: this.fb.control<string>('', [
        Validators.required,
        Validators.email,
      ]),
      password: this.fb.control<string>('', [Validators.required]),
    });
  }

  processLogin() {
    this.subscription = this.loginSvc.onLogin.subscribe({
      next: (value: any) => {
        let response = value as LoginResponse;
        if (response.result == true) {
          console.info(
            'we are setting this token from onlogin subject: ' + response.token
          );
          sessionStorage.setItem('cornToken', response.token);
          this.passwordiswrong = false;
          this.router.navigate(['/home']);
          this.subscription.unsubscribe();
        } else if (
          response.message == 'Email not found. Please register first.'
        ) {
          alert(response.message);
          this.subscription.unsubscribe();
        } else if (response.message == 'Login unsuccessful.') {
          alert('Login unsuccessful, please try again.');
          this.passwordiswrong = true;
          this.subscription.unsubscribe();
        }
      },
    });

    let loginUser = {} as Login;
    loginUser.email = this.loginForm.value.email;
    loginUser.password = this.loginForm.value.password;
    this.loginSvc.login(loginUser);
  }
  ngOnDestroy(): void {
    if (this.subscription) this.subscription.unsubscribe();
    if (this.checkToken) this.checkToken.unsubscribe();
  }
}
