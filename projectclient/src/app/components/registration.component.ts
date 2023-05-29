import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RegistrationService } from '../registration.service';
import { GenericResponse, Registration } from '../models';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit, OnDestroy {
  hide = true;
  emailAddressExists: boolean = false;
  registrationForm!: FormGroup;
  subscriptionForRegistration!: Subscription;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private registrationSvc: RegistrationService
  ) {}

  ngOnInit(): void {
    this.registrationForm = this.fb.group({
      username: this.fb.control<string>('', [Validators.required]),
      email: this.fb.control<string>('', [
        Validators.required,
        Validators.email,
      ]),
      password: this.fb.control<string>('', [
        Validators.required,
        Validators.minLength(8),
      ]),
      gender: this.fb.control<string>('', [Validators.required]),
    });
  }

  processRegistration() {
    this.subscriptionForRegistration =
      this.registrationSvc.onRegister.subscribe({
        next: (data: any) => {
          let response = data as GenericResponse;
          if (response.result == true) {
            this.registrationForm.reset();
            alert('You registered successfully!');
            this.router.navigate(['/']);
            this.subscriptionForRegistration.unsubscribe();
          } else if (response.message == 'The email exists.') {
            this.emailAddressExists = true;
            this.subscriptionForRegistration.unsubscribe();
          } else {
            alert(response.message);
            this.subscriptionForRegistration.unsubscribe();
          }
        },
      });

    const r = {} as Registration;
    r.userEmail = this.registrationForm.value.email;
    r.userName = this.registrationForm.value.username;
    r.userPassword = this.registrationForm.value.password;
    r.gender = this.registrationForm.value.gender;

    this.registrationSvc.register(r);
  }
  ngOnDestroy(): void {
    if (this.subscriptionForRegistration) {
      this.subscriptionForRegistration.unsubscribe();
    }
  }

  switchOffHint() {
    this.emailAddressExists = false;
  }
}
