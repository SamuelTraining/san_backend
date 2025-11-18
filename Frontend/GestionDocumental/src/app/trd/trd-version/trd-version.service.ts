import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrdVersion } from './trd-version.model';

@Injectable({
  providedIn: 'root'
})
export class TrdVersionService {
  // URL base que coincide con tu backend Spring Boot
  private readonly API_BASE_URL = 'http://localhost:8080/api/trd/version';

  constructor(private http: HttpClient) {}

  // Obtener todas las versiones TRD
  obtenerVersiones(): Observable<TrdVersion[]> {
    return this.http.get<TrdVersion[]>(this.API_BASE_URL);
  }

  // Crear o subir una nueva versión TRD (usa multipart/form-data)
  crearVersion(formData: FormData): Observable<TrdVersion> {
    return this.http.post<TrdVersion>(this.API_BASE_URL, formData);
  }

  // Generar la URL para descargar un archivo
  getArchivoUrl(id: number): string {
    return `${this.API_BASE_URL}/${id}/descargar`;
  }
}
