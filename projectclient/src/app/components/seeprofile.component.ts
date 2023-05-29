import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
import { LikeService } from '../like.service';
import { UserService } from '../user.service';

@Component({
  selector: 'app-seeprofile',
  templateUrl: './seeprofile.component.html',
  styleUrls: ['./seeprofile.component.css'],
})
export class SeeprofileComponent implements OnInit, OnDestroy {
  cornToken!: string;
  checkToken!: Subscription;
  getProfile!: Subscription;
  currentemail!: string;
  currentProfileId: number = 0;
  currentProfile = {} as ProfileResponse;
  photourl: string = '';
  photo: string = '';
  distance: string = '';
  likes: number = 0;
  checks: number = 0;
  checked: boolean = false;
  getDistance!: Subscription;
  status!: string;
  likeSub!: Subscription;
  userDetailsSub!: Subscription;

  constructor(
    private router: Router,
    private tokenSvc: TokenService,
    private activatedRoute: ActivatedRoute,
    private distanceSvc: DistanceService,
    private profileSvc: ProfileService,
    private likeSvc: LikeService,
    private userSvc: UserService
  ) {}

  ngOnInit(): void {
    this.currentProfileId = this.activatedRoute.snapshot.params['profileId'];
    console.info('displaying the profile for profile ' + this.currentProfileId);
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
            this.getProfile =
              this.profileSvc.onGetProfileThatLikedUser.subscribe({
                next: (p: any) => {
                  this.currentProfile = p as ProfileResponse;
                  if (this.currentProfile.result == false) {
                    if (
                      this.currentProfile.message ==
                      'The user was already matched.'
                    ) {
                      alert('You are already matched with this profile!');
                      this.router.navigate(['/matchedlist', p.profileId]);
                    } else if (
                      this.currentProfile.message ==
                      'This was not a valid request.'
                    ) {
                      alert(this.currentProfile.message);
                      this.router.navigate(['/likelist']);
                    }
                  } else {
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
                  }
                  this.getProfile.unsubscribe();
                },
              });
            let token = sessionStorage.getItem('cornToken');
            if (token == null) {
              token = '';
            }
            this.userSvc.getUser(token, this.currentemail);
            this.profileSvc.getProfileThatLikesUser(
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
    }
  }

  ngOnDestroy(): void {
    if (this.checkToken) this.checkToken.unsubscribe();
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
  like() {
    this.likeSub = this.likeSvc.onLikeBack.subscribe({
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
          alert(message);
          this.likeSub.unsubscribe();
          this.router.navigate(['/likelist']);
        }
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }
    this.likeSvc.likeAProfileBack(
      token,
      this.currentemail,
      this.currentProfile.profileId
    );
  }
}
