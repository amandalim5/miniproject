import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { TokenService } from '../token.service';
import {
  DistanceResponse,
  GenericResponse,
  ProfileResponse,
  TokenResponse,
  UserResponse,
} from '../models';
import { ProfileService } from '../profile.service';
import { DistanceService } from '../distance.service';
import { UserService } from '../user.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { LikeService } from '../like.service';

@Component({
  selector: 'app-profilelist',
  templateUrl: './profilelist.component.html',
  styleUrls: ['./profilelist.component.css'],
})
export class ProfilelistComponent implements OnInit, OnDestroy {
  cornToken!: string;
  checkToken!: Subscription;
  getProfileList!: Subscription;
  getProfile!: Subscription;
  currentemail!: string;
  currentProfile = {} as ProfileResponse;
  currentIndex: number = 0;
  listOfProfiles!: ProfileResponse[];
  checked: boolean = false;
  getDistance!: Subscription;
  userDetailsSub!: Subscription;
  likes: number = 0;
  checks: number = 0;
  listForm!: FormGroup;
  searchTerm: string = '';
  likeSub!: Subscription;
  photourl: string = '';
  photo: string = '';

  status!: string;
  distance: string = '';

  constructor(
    private router: Router,
    private tokenSvc: TokenService,
    private profileSvc: ProfileService,
    private distanceSvc: DistanceService,
    private userSvc: UserService,
    private fb: FormBuilder,
    private likeSvc: LikeService
  ) {}

  ngOnInit(): void {
    this.listForm = this.fb.group({
      search: this.fb.control<string>(''),
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
            sessionStorage.removeItem('cornToken');
            this.currentemail = response.useremail;
            sessionStorage.setItem('cornToken', response.newToken);
            this.checkToken.unsubscribe();
            this.userDetailsSub = this.userSvc.onGetUser.subscribe({
              next: (res: any) => {
                let userDetails = res as UserResponse;
                console.info(
                  'this user has this number of likes left: ' +
                    userDetails.likes
                );
                this.likes = userDetails.likes;
                console.info(
                  'this user has this number of checks left: ' +
                    userDetails.checks
                );
                this.checks = userDetails.checks;
                this.userDetailsSub.unsubscribe();
              },
            });

            this.getProfileList = this.profileSvc.onGetAllProfileId.subscribe({
              next: (res: any) => {
                if (res == null) {
                  alert('There are no profiles available to see yet...');
                  this.getProfileList.unsubscribe();
                } else {
                  this.listOfProfiles = res as ProfileResponse[];
                  if (this.listOfProfiles) {
                    this.getProfile = this.profileSvc.onGetProfile.subscribe({
                      next: (p: any) => {
                        this.currentProfile = p as ProfileResponse;
                        sessionStorage.removeItem('cornToken');
                        sessionStorage.setItem(
                          'cornToken',
                          this.currentProfile.token
                        );
                        this.photo = p.photo;
                        if (this.photo == '') {
                          this.photourl =
                            'https://cdn-icons-png.flaticon.com/512/877/877698.png';
                        } else {
                          this.photourl = p.photoUrl;
                        }
                        this.getProfile.unsubscribe();
                      },
                    });
                    let token = sessionStorage.getItem('cornToken');
                    if (token == null) {
                      token = '';
                    }
                    this.profileSvc.getProfileById(
                      token,
                      this.listOfProfiles[
                        this.currentIndex
                      ].profileId.toString(),
                      this.currentemail
                    );
                    console.info(
                      this.listOfProfiles[this.currentIndex].profileId +
                        ' <========== this is the profile id...'
                    );
                  }
                  this.getProfileList.unsubscribe();
                }
              },
            });
            let token = sessionStorage.getItem('cornToken');
            if (token == null) {
              token = '';
            }
            this.userSvc.getUser(token, this.currentemail);
            this.profileSvc.getProfileIds(token, this.currentemail);
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

  getNextProfile() {
    this.checked = false;
    this.status = '';
    this.distance = '';
    this.currentIndex++;
    if (this.listOfProfiles.length <= this.currentIndex) {
      // outside bounds
      this.currentIndex -= this.listOfProfiles.length;
    }
    this.getProfile = this.profileSvc.onGetProfile.subscribe({
      next: (p: any) => {
        this.currentProfile = p as ProfileResponse;
        if (this.currentProfile == null) {
          sessionStorage.removeItem('cornToken');
          this.getProfile.unsubscribe();
          alert('Your session has expired. Bringing you to the login page!');
          this.router.navigate(['/login']);
        } else {
          console.info(
            this.currentProfile.displayName +
              ' <============ this is from profilelist comp ts'
          );
          sessionStorage.removeItem('cornToken');
          sessionStorage.setItem('cornToken', this.currentProfile.token);
          this.photo = p.photo;
          if (this.photo == '') {
            this.photourl =
              'https://cdn-icons-png.flaticon.com/512/877/877698.png';
          } else {
            this.photourl = p.photoUrl;
          }
          this.getProfile.unsubscribe();
        }
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    this.profileSvc.getProfileById(
      token,
      this.listOfProfiles[this.currentIndex].profileId.toString(),
      this.currentemail
    );
  }

  getPreviousProfile() {
    this.checked = false;
    this.status = '';
    this.distance = '';
    this.currentIndex--;
    if (this.currentIndex < 0) {
      // outside bounds
      this.currentIndex = this.listOfProfiles.length - 1;
    }
    this.getProfile = this.profileSvc.onGetProfile.subscribe({
      next: (p: any) => {
        this.currentProfile = p as ProfileResponse;
        if (this.currentProfile == null) {
          sessionStorage.removeItem('cornToken');
          this.getProfile.unsubscribe();
          alert('Your session has expired. Bringing you to the login page!');
          this.router.navigate(['/login']);
        } else {
          console.info(
            this.currentProfile.displayName +
              ' <============ this is from profilelist comp ts'
          );
          sessionStorage.removeItem('cornToken');
          sessionStorage.setItem('cornToken', this.currentProfile.token);
          this.photo = p.photo;
          if (this.photo == '') {
            this.photourl =
              'https://cdn-icons-png.flaticon.com/512/877/877698.png';
          } else {
            this.photourl = p.photoUrl;
          }
          this.getProfile.unsubscribe();
        }
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    this.profileSvc.getProfileById(
      token,
      this.listOfProfiles[this.currentIndex].profileId.toString(),
      this.currentemail
    );
  }

  processDistance() {
    console.info(this.checks + ' how many??????????????????????');
    if (this.checks > 0) {
      this.checks--;
      this.checked = true;
      this.getDistance = this.distanceSvc.onDistance.subscribe({
        next: (res: any) => {
          let result = res as DistanceResponse;

          this.status = result.status;
          if (this.status == 'User has no profile') {
            alert('Create a profile with a postal code to use this function!');
            this.checks++;
          } else if (this.status == 'User has no postal code in profile') {
            alert('Your profile does not have a postal code...');
            this.checks++;
          } else {
            if (this.status != 'OK') {
              alert('One of your postal codes were invalid...');
            } else {
              this.distance = result.distance;
              alert(
                this.currentProfile.displayName +
                  ' lives ' +
                  this.distance +
                  ' away from you!'
              );
            }
          }

          this.getDistance.unsubscribe();
        },
      });
      let token = sessionStorage.getItem('cornToken');
      if (token == null) {
        token = '';
      }

      this.distanceSvc.getDistanceWithEmail(
        token,
        this.currentemail,
        this.currentProfile.profileId
      );
    } else {
      alert('You have run out of checks...');
    }
  }

  search() {
    let s = this.listForm.value.search;
    this.getProfileList = this.profileSvc.onGetAllProfileId.subscribe({
      next: (res: any) => {
        if (res == null) {
          this.listOfProfiles = res as ProfileResponse[];
          this.getProfileList.unsubscribe();
          alert('Try to search for something else!');
        } else {
          this.listOfProfiles = res as ProfileResponse[];
          if (this.listOfProfiles.length == 1) {
            alert('We found ' + this.listOfProfiles.length + ' profile!');
          } else {
            alert('We found ' + this.listOfProfiles.length + ' profiles!');
          }

          console.info(
            this.listOfProfiles.at(0)?.displayName +
              ' ============== in the component ts file'
          );

          this.getProfile = this.profileSvc.onGetProfile.subscribe({
            next: (p: any) => {
              this.currentProfile = p as ProfileResponse;
              console.info(
                this.currentProfile.displayName +
                  ' <============ this is from profilelist comp ts'
              );
              sessionStorage.removeItem('cornToken');
              sessionStorage.setItem('cornToken', this.currentProfile.token);
              this.photo = p.photo;
              if (this.photo == '') {
                this.photourl =
                  'https://cdn-icons-png.flaticon.com/512/877/877698.png';
              } else {
                this.photourl = p.photoUrl;
              }
              this.getProfile.unsubscribe();
            },
          });
          let token = sessionStorage.getItem('cornToken');
          if (token == null) {
            token = '';
          }
          this.profileSvc.getProfileById(
            token,
            this.listOfProfiles[this.currentIndex].profileId.toString(),
            this.currentemail
          );
          console.info(
            this.listOfProfiles[this.currentIndex].profileId +
              ' <========== this is the profile id...'
          );

          this.getProfileList.unsubscribe();
        }
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    this.profileSvc.getProfileIdsBySearch(token, this.currentemail, s);
  }

  searchByName() {
    let s = this.listForm.value.search;
    this.getProfileList = this.profileSvc.onGetAllProfileId.subscribe({
      next: (res: any) => {
        if (res == null) {
          this.listOfProfiles = res as ProfileResponse[];
          this.getProfileList.unsubscribe();
          alert('Try to search for something else!');
        } else {
          this.listOfProfiles = res as ProfileResponse[];
          if (this.listOfProfiles.length == 1) {
            alert('We found ' + this.listOfProfiles.length + ' profile!');
          } else {
            alert('We found ' + this.listOfProfiles.length + ' profiles!');
          }
          if (this.listOfProfiles) {
            this.getProfile = this.profileSvc.onGetProfile.subscribe({
              next: (p: any) => {
                this.currentProfile = p as ProfileResponse;
                sessionStorage.removeItem('cornToken');
                sessionStorage.setItem('cornToken', this.currentProfile.token);
                this.photo = p.photo;
                if (this.photo == '') {
                  this.photourl =
                    'https://cdn-icons-png.flaticon.com/512/877/877698.png';
                } else {
                  this.photourl = p.photoUrl;
                }
                this.getProfile.unsubscribe();
              },
            });
            let token = sessionStorage.getItem('cornToken');
            if (token == null) {
              token = '';
            }
            this.profileSvc.getProfileById(
              token,
              this.listOfProfiles[this.currentIndex].profileId.toString(),
              this.currentemail
            );
            console.info(
              this.listOfProfiles[this.currentIndex].profileId +
                ' <========== this is the profile id...'
            );
          }
          this.getProfileList.unsubscribe();
        }
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    this.profileSvc.getProfileIdsByName(token, this.currentemail, s);
  }

  like() {
    console.info(
      this.currentemail + '++++++++++++++++ checking for like function'
    );
    if (this.likes <= 0) {
      alert('You have run out of likes! Likes refresh at 7:15 pm');
    } else {
      this.likeSub = this.likeSvc.onLike.subscribe({
        next: (res: any) => {
          let response = res as GenericResponse;
          let message = response.message;
          console.info(message + ' ++++++++++++++++ I want to see this');
          if (
            message == 'You have no profile yet. Please create one first!' ||
            message == 'Your profile is not public.' ||
            message == 'You already liked this profile!'
          ) {
            alert(message);
            this.likeSub.unsubscribe();
          } else {
            this.likes--;
            alert(message);
            this.likeSub.unsubscribe();
          }
        },
      });
      let token = sessionStorage.getItem('cornToken');
      if (token == null) {
        token = '';
      }
      this.likeSvc.likeAProfile(
        token,
        this.currentemail,
        this.currentProfile.profileId
      );
    }
  }
}
