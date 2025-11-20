import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
// CORRECCIÓN AQUÍ: Apuntando a la carpeta y archivo correctos
import { TrdVersionComponent } from './trd/trd-version/trd-version.component';

@Component({
  selector: 'app-root',
  standalone: true,
  // Importamos el componente aquí para poder usarlo en el HTML
  imports: [CommonModule, RouterOutlet, TrdVersionComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css'] // Asegúrate de que sea 'styleUrls' (plural) o 'styleUrl' (singular) según tu versión
})
export class App {
  title = 'GestionDocumental';
}