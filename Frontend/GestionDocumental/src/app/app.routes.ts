import { Routes } from '@angular/router';
import { TrdVersionComponent } from './trd/trd-version/trd-version.component';
import { ValidacionComponent } from './validacion/validacion.component'; // <-- Importante

export const routes: Routes = [
  // La ruta 'trd' carga la pantalla de subir archivos
  { path: 'trd', component: TrdVersionComponent },
  
  // La ruta 'validacion' DEBE cargar ValidacionComponent (la tabla)
  { path: 'validacion', component: ValidacionComponent }, 
  
  // Redirección por defecto
  { path: '', redirectTo: 'trd', pathMatch: 'full' },
  { path: '**', redirectTo: 'trd' }
];