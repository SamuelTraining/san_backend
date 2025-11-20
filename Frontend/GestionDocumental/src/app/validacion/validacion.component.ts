import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-validacion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './validacion.component.html',
  styleUrls: ['./validacion.component.css']
})
export class ValidacionComponent implements OnInit {
  pendientes: any[] = [];
  // Estas son las carpetas reales que creará el sistema
  categorias = ['Contratos', 'Nominas', 'Actas', 'Facturas', 'Otros'];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.cargarPendientes();
  }

  cargarPendientes() {
    this.http.get<any[]>('http://localhost:8080/api/validacion/pendientes')
      .subscribe({
        next: (data) => {
          this.pendientes = data;
          // Preparamos el campo para el dropdown
          this.pendientes.forEach(p => p.nuevaCategoria = ''); 
        },
        error: (e) => console.error("Error cargando pendientes", e)
      });
  }

  validar(archivo: any) {
    if (!archivo.nuevaCategoria) return;

    if (confirm(`¿Mover este archivo a la carpeta "${archivo.nuevaCategoria}"?`)) {
      const url = `http://localhost:8080/api/validacion/${archivo.id}/confirmar`;
      
      this.http.post(url, { categoria: archivo.nuevaCategoria }).subscribe({
        next: () => {
          alert('✅ Archivo corregido y movido exitosamente.');
          this.cargarPendientes(); // Recargar la lista
        },
        error: (err) => {
            console.error(err);
            // Intentamos leer el mensaje de error que envía Java
            const mensaje = err.error || err.message || 'Error desconocido';
            alert('❌ Error al validar: ' + mensaje);
        }
      });
    }
  }
}