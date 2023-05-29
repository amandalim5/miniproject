import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { TokenService } from '../token.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-password',
  templateUrl: './password.component.html',
  styleUrls: ['./password.component.css'],
})
export class PasswordComponent implements OnInit, OnDestroy {
  hide = true;
  emailValid!: boolean;
  subscription!: Subscription;
  tokenVerfied!: boolean;
  changePasswordSub!: Subscription;
  resetForm!: FormGroup;
  verifyForm!: FormGroup;
  submitted = false;
  canResend = true;
  theEmail: string = '';
  message: string = '';
  format: string = 'Resend the verification code in ';
  constructor(
    private fb: FormBuilder,
    private tokenSvc: TokenService,
    private router: Router
  ) {}
  ngOnInit(): void {
    this.resetForm = this.fb.group({
      email: this.fb.control<string>('', [Validators.email]),
    });
    this.verifyForm = this.fb.group({
      code: this.fb.control<string>(''),
      password: this.fb.control<string>(''),
    });
  }

  sendToken() {
    this.subscription = this.tokenSvc.onReset.subscribe({
      next: (result: any) => {
        if (result == true) {
          this.emailValid = true;
          this.theEmail = this.resetForm.value.email;
          if (!this.canResend) {
            this.canResend = true;
          }
          this.submitted = true;
          let counter = 10;
          this.message = this.format + counter + ' seconds';
          counter--;
          let changeTheMessage = setInterval(() => {
            this.message = this.format + counter + ' seconds';
            // console.info('this is the counter: ' + counter);
            counter--;
          }, 1000);
          setTimeout(() => {
            clearInterval(changeTheMessage);
            this.message =
              'Click "Resend" if you have not received the verification code';
          }, 10000);

          setTimeout(() => {
            this.canResend = false;
          }, 10000);
          this.subscription.unsubscribe();
        } else {
          this.emailValid = false;
          this.subscription.unsubscribe();
        }
      },
    });
    this.tokenSvc.sendToken(this.resetForm.value.email);
  }

  changePassword() {
    this.changePasswordSub = this.tokenSvc.onVerify.subscribe({
      next: (result: any) => {
        if (result == true) {
          alert('The password was successfully changed!');
          this.router.navigate(['/']);
          this.changePasswordSub.unsubscribe();
        } else {
          alert('Please check if the token is correct.');
          this.changePasswordSub.unsubscribe();
        }
      },
    });
    this.tokenSvc.verifyPasswordToken(
      this.theEmail,
      this.verifyForm.value.code,
      this.verifyForm.value.password
    );
  }
  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.changePasswordSub) {
      this.changePasswordSub.unsubscribe();
    }
  }
}
