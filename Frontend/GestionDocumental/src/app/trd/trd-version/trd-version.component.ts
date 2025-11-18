import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrdVersionService } from './trd-version.service';
import { TrdVersion, TrdVersionCreate } from './trd-version.model';

@Component({
  selector: 'app-trd-version',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trd-version.component.html',
  styleUrls: ['./trd-version.component.css']
})
export class TrdVersionComponent implements OnInit {
  trdVersiones: TrdVersion[] = [];
  selectedFile: File | null = null;

  trdVersion: TrdVersionCreate = {
    nombre: '',
    vigenciaDesde: '',
    vigenciaHasta: '',
    estado: 'BORRADOR',
    archivo: null
  };

  constructor(private svc: TrdVersionService) {}

  ngOnInit(): void {
    this.cargarVersiones();
  }

  // Captura el archivo del input
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.trdVersion.archivo = file;
      this.selectedFile = file;
      console.log('✅ Archivo seleccionado:', file.name);
    } else {
      this.trdVersion.archivo = null;
      this.selectedFile = null;
    }
  }

  // Guardar nueva versión TRD
  guardarTrdVersion(event?: Event): void {
    event?.preventDefault();

    const vigenciaDesde = this.trdVersion.vigenciaDesde?.toString().split('T')[0] || '';
    const vigenciaHasta = this.trdVersion.vigenciaHasta?.toString().split('T')[0] || '';

    if (!this.trdVersion.nombre.trim() || !vigenciaDesde || !this.selectedFile) {
      alert('Por favor complete los campos requeridos (nombre, vigencia desde y archivo).');
      return;
    }

    const formData = new FormData();
    formData.append('nombre', this.trdVersion.nombre.trim());
    formData.append('vigenciaDesde', vigenciaDesde);
    if (vigenciaHasta) formData.append('vigenciaHasta', vigenciaHasta);
    formData.append('estado', this.trdVersion.estado);
    formData.append('file', this.selectedFile!, this.selectedFile!.name);

    this.svc.crearVersion(formData).subscribe({
      next: (response) => {
        alert(`✅ TRD versión "${response.nombre}" creada correctamente`);
        this.limpiarFormulario();
        this.cargarVersiones();
      },
      error: (err) => {
        console.error('❌ Error al guardar TRD', err);
        let mensaje = 'Error al guardar la TRD';
        if (err.status === 0) {
          mensaje += ': No se pudo conectar con el servidor.';
        } else if (err.error?.message) {
          mensaje += ': ' + err.error.message;
        } else {
          mensaje += ': ' + err.statusText;
        }
        alert(mensaje);
      }
    });
  }

  limpiarFormulario(): void {
    this.trdVersion = {
      nombre: '',
      vigenciaDesde: '',
      vigenciaHasta: '',
      estado: 'BORRADOR',
      archivo: null
    };
    this.selectedFile = null;
    const fileInput = document.getElementById('archivo') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  cargarVersiones(): void {
    this.svc.obtenerVersiones().subscribe({
      next: (data) => (this.trdVersiones = data || []),
      error: (err) => {
        console.error('⚠️ Error al listar TRD', err);
        this.trdVersiones = [];
      }
    });
  }

  getDownloadUrl(id: number | undefined): string {
    return id ? this.svc.getArchivoUrl(id) : '#';
  }
}
