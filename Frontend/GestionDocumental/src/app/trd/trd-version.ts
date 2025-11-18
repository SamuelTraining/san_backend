import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-trd-version',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trd-version/trd-version.component.html',
  styleUrls: ['./trd-version/trd-version.component.css']
})
export class TrdVersionComponent implements OnInit {

  // Mensajes o notificaciones
  mensaje: any;
  mensajeTipo:
    | string
    | string[]
    | Set<string>
    | { [klass: string]: any }
    | null
    | undefined;

  // Variables de formulario
  nombre: string = '';
  vigenciaDesde: string = '';
  vigenciaHasta: string = '';
  estado: string = 'ACTIVO';
  selectedFile: File | null = null; // ✅ agregado

  // Estructura para guardar y mostrar versiones
  trdVersion: any = {
    nombre: '',
    descripcion: '',
    fechaCreacion: '',
    estado: '',
    archivo: null
  };

  trdVersiones: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.cargarTrdVersiones();
  }

  // ✅ Captura el archivo del input
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.trdVersion.archivo = file;
      this.selectedFile = file;
    } else {
      this.trdVersion.archivo = null;
      this.selectedFile = null;
    }
  }

  // ✅ Guarda la versión TRD enviando FormData
  guardarTrdVersion(event?: Event) {
    event?.preventDefault();

    if (!this.trdVersion.nombre || !this.trdVersion.fechaCreacion || !this.selectedFile) {
      alert('Por favor complete todos los campos requeridos y seleccione un archivo.');
      return;
    }

    // Crea el FormData para enviar texto + archivo
    const formData = new FormData();
    formData.append('nombre', this.trdVersion.nombre);
    formData.append('descripcion', this.trdVersion.descripcion || '');
    formData.append('fechaCreacion', this.trdVersion.fechaCreacion);
    formData.append('estado', this.trdVersion.estado);
    if (this.selectedFile) {
      formData.append('archivo', this.selectedFile, this.selectedFile.name);
    }

    this.http.post('http://localhost:8080/api/trd', formData)
      .subscribe({
        next: (res) => {
          alert('Versión TRD guardada correctamente.');
          this.trdVersion = { nombre: '', descripcion: '', fechaCreacion: '', estado: '', archivo: null };
          this.selectedFile = null;
          this.cargarTrdVersiones();
        },
        error: (err) => {
          console.error('Error al guardar TRD:', err);
          alert('Error al guardar TRD: ' + (err?.error || err?.message || err));
        }
      });
  }

  // ✅ Carga las versiones existentes
  cargarTrdVersiones() {
    this.http.get<any[]>('http://localhost:8080/api/trd')
      .subscribe({
        next: (res) => this.trdVersiones = res || [],
        error: (err) => {
          console.error('Error cargando versiones TRD', err);
          this.trdVersiones = [];
        }
      });
  }

  // ✅ Obtiene URL de descarga (opcional)
  getDownloadUrl(id: number | undefined): string {
    if (!id) return '#';
    return `http://localhost:8080/api/trd/${id}/archivo`;
  }
}
