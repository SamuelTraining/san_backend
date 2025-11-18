export interface TrdVersion {
    id: number;
    nombre: string;
    vigenciaDesde: string;  // LocalDate en formato ISO
    vigenciaHasta?: string; // LocalDate en formato ISO
    estado: 'VIGENTE' | 'HISTORICA' | 'BORRADOR';
    nombreArchivo: string | null;
    tipoArchivo: string | null;
    fechaCarga?: string;    // OffsetDateTime en formato ISO
}

export interface TrdVersionCreate {
    nombre: string;
    vigenciaDesde: string;  // LocalDate en formato ISO
    vigenciaHasta?: string; // LocalDate en formato ISO
    estado: string;
    archivo: File | null;   // Para el campo @Lob byte[] archivo
}