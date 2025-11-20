import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router'; // Importante para la redirección
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
  
  // Variable para el archivo seleccionado
  selectedFile: File | null = null;

  trdVersion: TrdVersionCreate = {
    nombre: '',
    vigenciaDesde: '',
    vigenciaHasta: '',
    estado: 'BORRADOR',
    archivo: null
  };

  // Inyectamos el Servicio y el Router
  constructor(private svc: TrdVersionService, private router: Router) {}

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
  guardarTrdVersion(event?: Event) {
    if (event) event.preventDefault();

    // 1. Validaciones explícitas
    if (!this.trdVersion.nombre || this.trdVersion.nombre.trim() === '') {
      alert("❌ Error: El campo 'Nombre' está vacío.");
      return;
    }

    if (!this.trdVersion.vigenciaDesde) {
      alert("❌ Error: El campo 'Vigencia Desde' no tiene fecha seleccionada.");
      return;
    }

    if (!this.selectedFile) {
      alert("❌ Error: No se ha seleccionado ningún archivo.");
      return;
    }

    // 2. Preparar datos para el envío
    const formData = new FormData();
    
    // Es importante que 'file' coincida con el @RequestPart("file") del backend
    formData.append('file', this.selectedFile, this.selectedFile.name);
    formData.append('nombre', this.trdVersion.nombre.trim());
    
    // Formatear fechas a YYYY-MM-DD
    const vigenciaDesde = this.trdVersion.vigenciaDesde.toString().split('T')[0];
    formData.append('vigenciaDesde', vigenciaDesde);

    if (this.trdVersion.vigenciaHasta) {
        const vigenciaHasta = this.trdVersion.vigenciaHasta.toString().split('T')[0];
        formData.append('vigenciaHasta', vigenciaHasta);
    }

    formData.append('estado', this.trdVersion.estado);

    // 3. Llamar al servicio
    this.svc.crearVersion(formData).subscribe({
      next: (response: any) => {
        console.log('Respuesta del servidor:', response);

        // Lógica de Redirección Inteligente (IA)
        if (response.clasificacion === 'REVISION') {
             const irAValidar = confirm(
                 `⚠️ ATENCIÓN: La IA tiene dudas sobre la clasificación de este archivo.\n\n` +
                 `Detalle: ${response.mensajeIA}\n` +
                 `El archivo se guardó como 'No Procesable'.\n\n` +
                 `¿Deseas ir a la bandeja de validación manual para corregirlo ahora?`
             );

             if (irAValidar) {
                 this.router.navigate(['/validacion']);
             } else {
                 this.limpiarFormulario();
                 this.cargarVersiones();
             }
        } else {
             // Caso exitoso normal
             alert(`✅ ¡Éxito! TRD cargada y clasificada.\n${response.mensajeIA || ''}`);
             this.limpiarFormulario();
             this.cargarVersiones();
        }
      },
      error: (err) => {
        console.error('Error backend:', err);
        alert('⚠️ Error del Servidor: ' + (err.error?.message || err.message || 'Revisar consola de Java'));
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