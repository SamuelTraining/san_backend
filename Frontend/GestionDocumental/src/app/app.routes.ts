import { Routes } from '@angular/router';
import { TrdVersionComponent } from './trd/trd-version/trd-version.component';

export const routes: Routes = [
  { path: 'trd', component: TrdVersionComponent },
  { path: '', redirectTo: 'trd', pathMatch: 'full' },
  { path: '**', redirectTo: 'trd' }
];
