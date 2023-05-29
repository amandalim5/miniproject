import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login.component';
import { PasswordComponent } from './components/password.component';
import { RegistrationComponent } from './components/registration.component';
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HomeComponent } from './components/home.component';
import { RegistrationService } from './registration.service';
import { HttpClientModule } from '@angular/common/http';
import { LoginService } from './login.service';
import { TokenService } from './token.service';
import { ProfileComponent } from './components/profile.component';
import { ChangepasswordComponent } from './components/changepassword.component';
import { UpdateService } from './update.service';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { DeleteaccountComponent } from './components/deleteaccount.component';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { PreviewComponent } from './components/preview.component';
import { MatCardModule } from '@angular/material/card';
import { DistanceService } from './distance.service';
import { ProfileService } from './profile.service';
import { ProfilelistComponent } from './components/profilelist.component';
import { UserService } from './user.service';
import { MatBadgeModule } from '@angular/material/badge';
import { LikeService } from './like.service';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { UploadService } from './upload.service';
import { ListoflikesComponent } from './components/listoflikes.component';
import { SeeprofileComponent } from './components/seeprofile.component';
import { MatchedlistComponent } from './components/matchedlist.component';
import { ChatService } from './chat.service';
import { MatDividerModule } from '@angular/material/divider';

const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'reset', component: PasswordComponent },
  { path: 'register', component: RegistrationComponent },
  { path: 'home', component: HomeComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'changepassword', component: ChangepasswordComponent },
  { path: 'deleteaccount', component: DeleteaccountComponent },
  { path: 'preview/:email', component: PreviewComponent },
  { path: 'lookaround', component: ProfilelistComponent },
  { path: 'likelist', component: ListoflikesComponent },
  { path: 'seeprofile/:profileId', component: SeeprofileComponent },
  { path: 'matchedlist/:profileId', component: MatchedlistComponent },
  { path: '**', redirectTo: '/', pathMatch: 'full' },
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    PasswordComponent,
    RegistrationComponent,
    HomeComponent,
    ProfileComponent,
    ChangepasswordComponent,
    DeleteaccountComponent,
    PreviewComponent,
    ProfilelistComponent,
    ListoflikesComponent,
    SeeprofileComponent,
    MatchedlistComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatSidenavModule,
    MatSlideToggleModule,
    RouterModule.forRoot(appRoutes),
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardModule,
    MatBadgeModule,
    MatProgressBarModule,
    MatDividerModule,
  ],
  providers: [
    RegistrationService,
    LoginService,
    TokenService,
    UpdateService,
    DistanceService,
    ProfileService,
    UserService,
    LikeService,
    UploadService,
    ChatService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
