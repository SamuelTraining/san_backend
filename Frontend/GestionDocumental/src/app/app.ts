import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TrdVersionComponent } from "./trd/trd-version";

@Component({
  selector: 'app-root',
  imports: [TrdVersionComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('GestionDocumental');
}
