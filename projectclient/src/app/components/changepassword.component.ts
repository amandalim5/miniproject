import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TokenService } from '../token.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UpdateService } from '../update.service';
import { GenericResponse, TokenResponse } from '../models';

@Component({
  selector: 'app-changepassword',
  templateUrl: './changepassword.component.html',
  styleUrls: ['./changepassword.component.css'],
})
export class ChangepasswordComponent {
  hide = true;
  confirmhide = true;
  passwordTheSame!: boolean;
  cornToken!: string;
  checkToken!: Subscription;
  existingUserEmail!: string;
  passwordForm!: FormGroup;
  subscription!: Subscription;

  constructor(
    private router: Router,
    private tokenSvc: TokenService,
    private fb: FormBuilder,
    private updateSvc: UpdateService
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
            this.existingUserEmail = response.useremail;
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

    this.passwordForm = this.fb.group({
      email: this.fb.control<string>(this.existingUserEmail),
      password: this.fb.control<string>('', [
        Validators.required,
        Validators.minLength(8),
      ]),
      confirmpassword: this.fb.control<string>('', [Validators.required]),
    });
    this.passwordForm.get('email')?.disable();
  }

  ngDoCheck(): void {
    if (this.passwordForm.controls['confirmpassword'].pristine) {
      this.passwordTheSame = true;
    } else {
      if (
        this.passwordForm.value.password ==
        this.passwordForm.value.confirmpassword
      ) {
        this.passwordTheSame = true;
      } else {
        this.passwordTheSame = false;
      }
    }
  }

  ngOnDestroy(): void {
    if (this.checkToken) this.checkToken.unsubscribe();
    if (this.subscription) this.subscription.unsubscribe();
  }

  processForm() {
    this.subscription = this.updateSvc.onUpdatePassword.subscribe({
      next: (response: any) => {
        let g = response as GenericResponse;
        console.info(
          g.message + ' ========= this was the message from the response'
        );
        if (g.result == true) {
          alert('The password was changed successfully!');
          this.passwordForm.get('password')?.reset();
          this.passwordForm.get('confirmpassword')?.reset();
          this.subscription.unsubscribe();
          this.router.navigate(['/home']);
        } else {
          this.subscription.unsubscribe();
          alert(
            'The password was not changed successfully. Error: ' + g.message
          );
        }
      },
      // error: () => {
      //   console.info(
      //     'There was an error in the processForm() in changePasswordComp'
      //   );
      // },
    });
    let token = sessionStorage.getItem('cornToken');
    console.info('this is the token... is it origin? >>>>>>>> ' + token);
    if (token == null) {
      token = '';
    }
    this.updateSvc.updatePassword(token, this.passwordForm.value.password);
  }
}
