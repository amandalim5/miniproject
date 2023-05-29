import { Component, OnInit, DoCheck, OnDestroy, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TokenService } from '../token.service';
import {
  GenericResponse,
  Profile,
  ProfileResponse,
  TokenResponse,
  UploadResult,
} from '../models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UpdateService } from '../update.service';
import { HttpClient } from '@angular/common/http';
import { UploadService } from '../upload.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit, OnDestroy {
  cornToken!: string;
  checkToken!: Subscription;
  getProfile!: Subscription;
  updateProfile!: Subscription;
  profileForm!: FormGroup;
  currentemail!: string;

  saved: boolean = false;
  result!: boolean;
  message!: string;
  profileId!: number;
  email: string = '';
  profileIsPublic: boolean = true;
  displayName: string = '';
  summary: string = '';
  birthday: number = 0;
  birthmonth: number = 0;
  birthyear: number = 0;
  height!: number;
  weight!: number;
  isSmoking: string = '';
  postalCode: string = '';
  thebirth!: Date;
  mail: string = '';
  fileName: string = '';

  requiredFileType: string = 'image/png';

  uploadProgress: number = 0;
  uploadSub!: Subscription;
  deleteSub!: Subscription;

  onFileSelected(event: any) {
    const file: Blob = event.target.files[0];
    const pic: File = event.target.files[0];

    if (pic) {
      this.fileName = pic.name;
      this.uploadSub = this.uploadSvc.onUploadPic.subscribe({
        next: (res: any) => {
          let response = res as UploadResult;
          console.info(response.imageKey);
        },
      });
      let token = sessionStorage.getItem('cornToken');
      if (token == null) {
        token = '';
      }
      this.uploadSvc.uploadPicture(token, this.currentemail, file);
    }
  }
  cancelUpload() {
    this.uploadSub.unsubscribe();
    this.reset();
  }
  reset() {
    this.uploadProgress = 0;
    this.uploadSub = new Subscription();
  }
  deletePhoto() {
    this.deleteSub = this.uploadSvc.onDeletePic.subscribe({
      next: (res: any) => {
        let response = res as GenericResponse;
        if (response.result == true) {
          this.fileName = '';
        } else {
          alert('Delete failed, please try again.');
        }
        this.deleteSub.unsubscribe();
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    this.uploadSvc.deletePicture(token, this.currentemail);
  }

  constructor(
    private router: Router,
    private tokenSvc: TokenService,
    private updateSvc: UpdateService,
    private fb: FormBuilder,
    private uploadSvc: UploadService
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      pdisplayName: this.fb.control<string>(this.displayName, [
        Validators.required,
      ]),
      pprofileIsPublic: this.fb.control<boolean>(this.profileIsPublic),
      psummary: this.fb.control<string>(this.summary, [Validators.required]),
      pbirthday: this.fb.control<Date | null>(null, [Validators.required]),
      pheight: this.fb.control<number | null>(null, [Validators.required]),
      pweight: this.fb.control<number | null>(null, [Validators.required]),
      pisSmoking: this.fb.control<string>(this.isSmoking, [
        Validators.required,
      ]),
      // todo: validator to ensure all all the characters are numbers in the postal code
      // check with google api if the postal code is valid?
      ppostalCode: this.fb.control<string>('', [
        Validators.minLength(6),
        Validators.maxLength(6),
      ]),
      pmail: this.fb.control<string>('', [Validators.required]),
    });
    let corntoken = sessionStorage.getItem('cornToken');
    if (corntoken == null) {
      this.router.navigate(['/login']);
    } else {
      this.cornToken = corntoken;
      this.checkToken = this.tokenSvc.onCheck.subscribe({
        next: (result: any) => {
          let response = result as TokenResponse;
          if (response.result == true) {
            console.info('we got here, GOOD');
            this.currentemail = response.useremail;
            sessionStorage.removeItem('cornToken');
            sessionStorage.setItem('cornToken', response.newToken);
            this.checkToken.unsubscribe();
            // get profile
            this.getProfile = this.updateSvc.onGetProfile.subscribe({
              next: (res: any) => {
                let profile = res as ProfileResponse;
                this.result = profile.result;
                if (profile.result == true) {
                  console.info(
                    'there was an existing profile! ' +
                      profile.displayName +
                      profile.birthyear
                  );
                  this.profileId = profile.profileId;
                  this.email = profile.email;
                  this.profileIsPublic = profile.profileIsPublic;
                  this.displayName = profile.displayName;
                  this.summary = profile.summary;
                  this.birthday = profile.birthday;
                  this.birthmonth = profile.birthmonth;
                  this.birthyear = profile.birthyear;
                  this.height = profile.height;
                  this.weight = profile.weight;
                  this.isSmoking = profile.isSmoking;
                  this.thebirth = new Date(
                    this.birthyear,
                    this.birthmonth - 1,
                    this.birthday
                  );
                  this.postalCode = profile.postalCode;
                  this.mail = profile.mail;
                  this.fileName = profile.photo;
                  console.info(
                    '+++++++++++++++++++ checking for mail message: ' +
                      this.mail
                  );
                  this.getProfile.unsubscribe();
                } else {
                  console.info('there was no existing profile ..... aiyoh');
                  this.getProfile.unsubscribe();
                }
              },
            });
            let token = sessionStorage.getItem('cornToken');
            if (token == null) {
              token = '';
            }
            this.updateSvc.getProfile(token, this.currentemail);
          } else {
            console.info('this was the exe:the axe');
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
    if (this.checkToken) {
      this.checkToken.unsubscribe();
    }
    if (this.getProfile) {
      this.getProfile.unsubscribe();
    }
    if (this.updateProfile) {
      this.updateProfile.unsubscribe();
    }
  }
  processProfile() {
    console.info('we got the date: ' + this.profileForm.value.pbirthday);
    let date = this.profileForm.value.pbirthday;
    // var dates_as_int = dates.map(Date.parse);
    console.info(
      'this is the integer date: ' +
        date.getDate() +
        ' and ' +
        (date.getYear() + 1900) +
        ' and ' +
        (date.getMonth() + 1)
    );
    console.info(
      'this is the public thingy: ' + this.profileForm.value.pprofileIsPublic
    );
    console.info(
      'this is the smoke thingy: ' + this.profileForm.value.pisSmoking
    );
    this.updateProfile = this.updateSvc.onUpdateProfile.subscribe({
      next: (data: any) => {
        let response = data as GenericResponse;
        if (response.result == true) {
          alert(response.message);
          // this.router.navigate(['/home']);
          this.saved = true;

          this.updateProfile.unsubscribe();
        } else {
          alert(response.message);
          this.router.navigate(['/home']);
          this.updateProfile.unsubscribe();
        }
      },
    });

    const p = {} as Profile;
    p.email = this.currentemail;
    p.profileIsPublic = this.profileForm.value.pprofileIsPublic;
    p.displayName = this.profileForm.value.pdisplayName;
    p.summary = this.profileForm.value.psummary;
    p.birthday = date.getDate();
    p.birthmonth = date.getMonth() + 1;
    p.birthyear = date.getYear() + 1900;
    p.height = this.profileForm.value.pheight;
    p.weight = this.profileForm.value.pweight;
    p.isSmoking = this.profileForm.value.pisSmoking;
    p.postalCode = this.profileForm.value.ppostalCode;
    if (this.profileForm.value.ppostalCode == null) {
      p.postalCode = '';
    }
    p.mail = this.profileForm.value.pmail;
    this.updateSvc.updateProfile(this.cornToken, p);
  }
}
