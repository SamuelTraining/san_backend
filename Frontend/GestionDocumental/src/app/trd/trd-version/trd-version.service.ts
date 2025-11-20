import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrdVersion } from './trd-version.model';

@Injectable({
  providedIn: 'root'
})
export class TrdVersionService {
  // URL base apuntando a tu Backend de Java
  private API_BASE_URL = 'http://localhost:8080/api/trd/version';

  constructor(private http: HttpClient) { }

  /**
   * Obtiene la lista de versiones (GET)
   */
  obtenerVersiones(): Observable<TrdVersion[]> {
    // Si tu controlador principal es TrdController, la URL de lista es diferente
    // Ajustamos para apuntar a /api/trd que es donde está la lista según tu Java
    return this.http.get<TrdVersion[]>('http://localhost:8080/api/trd');
  }

  /**
   * Sube una nueva versión con archivo (POST)
   */
  crearVersion(formData: FormData): Observable<TrdVersion> {
    // Este apunta a TrdVersionController que maneja la subida
    return this.http.post<TrdVersion>(this.API_BASE_URL, formData);
  }

  /**
   * Genera la URL para descargar el archivo
   */
  getArchivoUrl(id: number): string {
    // Ajusta esto según la ruta real de descarga en tu backend si existe,
    // o déjalo así si planeas implementarlo luego.
    return `${this.API_BASE_URL}/${id}/archivo`; 
  }
}