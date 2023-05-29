import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../token.service';
import { Subscription } from 'rxjs';
import { DistanceResponse, ProfileResponse, TokenResponse } from '../models';
import { UpdateService } from '../update.service';
import { DistanceService } from '../distance.service';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css'],
})
export class PreviewComponent implements OnInit, OnDestroy {
  cornToken!: string;
  checkToken!: Subscription;
  getProfile!: Subscription;
  getDistance!: Subscription;
  currentemail!: string;
  checkedliao: boolean = false;
  photo: string = '';
  photourl: string = '';

  profileId!: number;
  displayName: string = '';
  summary: string = '';
  height!: number;
  weight!: number;
  isSmoking: string = '';
  postalCode: string = '';
  thebirth!: Date;
  theAge!: number;

  status!: string;
  distance: string = '';

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private tokenSvc: TokenService,
    private updateSvc: UpdateService,
    private distanceSvc: DistanceService
  ) {}

  ngOnInit(): void {
    const userEmail = this.activatedRoute.snapshot.params['email'];
    console.info('displaying the profile for ' + userEmail);
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
            this.getProfile = this.updateSvc.onGetProfile.subscribe({
              next: (res: any) => {
                let profile = res as ProfileResponse;
                this.profileId = profile.profileId;
                this.displayName = profile.displayName;
                this.summary = profile.summary;
                this.theAge = profile.age;
                this.height = profile.height;
                this.weight = profile.weight;
                this.isSmoking = profile.isSmoking;
                this.photo = profile.photo;
                if (this.photo == '') {
                  this.photourl =
                    'https://cdn-icons-png.flaticon.com/512/877/877698.png';
                } else {
                  this.photourl = profile.photoUrl;
                }
                if (profile.postalCode == '') {
                  this.checkedliao = true;
                }
                this.getProfile.unsubscribe();
              },
            });
            let token = sessionStorage.getItem('cornToken');
            if (token == null) {
              token = '';
            }
            this.updateSvc.getProfile(token, this.currentemail);
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
    if (this.getProfile) this.getProfile.unsubscribe();
    if (this.getDistance) this.getDistance.unsubscribe();
  }

  processDistance() {
    this.checkedliao = true;
    this.getDistance = this.distanceSvc.onDistance.subscribe({
      next: (res: any) => {
        let result = res as DistanceResponse;
        this.status = result.status;

        if (this.status != 'OK') {
          alert('Your postal code was not valid...');
        } else {
          this.distance = result.distance;
          alert(
            this.displayName + ' lives ' + this.distance + ' away from you!'
          );
        }
        this.getDistance.unsubscribe();
      },
    });
    let token = sessionStorage.getItem('cornToken');
    if (token == null) {
      token = '';
    }

    this.distanceSvc.getDistance(token, this.profileId, -1);
  }
}
