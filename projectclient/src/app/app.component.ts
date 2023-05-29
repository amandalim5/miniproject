import { Component, DoCheck } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements DoCheck {
  title = 'projectclient';
  tokenExists!: boolean;
  constructor(private router: Router) {}
  ngDoCheck(): void {
    if (sessionStorage.getItem('cornToken')) {
      this.tokenExists = true;
    } else {
      this.tokenExists = false;
    }
  }
  logout() {
    alert('Logging out...');
    sessionStorage.removeItem('cornToken');
    this.router.navigate(['/login']);
  }
  goHome() {
    this.router.navigate(['/home']);
  }
}
