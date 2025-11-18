package com.app.gestiondocumental.config;

import com.app.gestiondocumental.model.*; // Importa todas tus clases del modelo
import com.app.gestiondocumental.repository.TrdVersionRepository; // Importa tu repositorio
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {
    private final TrdVersionRepository trdRepo;

    public DataLoader(TrdVersionRepository trdRepo) {
        this.trdRepo = trdRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (trdRepo.count() > 0) return;

        TrdVersion t = new TrdVersion();
        t.setNombre("TRD 2025");
        t.setVigenciaDesde(LocalDate.of(2025, 1, 1));
        t.setVigenciaHasta(null);
        t.setEstado("vigente");

        // serie
        Serie s = new Serie();
        s.setCodigo("GH");
        s.setNombre("Gestión Humana");

        // subserie
        Subserie ss = new Subserie();
        ss.setCodigo("NOM");
        ss.setNombre("Nómina");

        // tipos documentales
        TipoDocumental td1 = new TipoDocumental();
        td1.setCodigo("SOL");
        td1.setNombre("Solicitud de pago");
        td1.setObligatoria(true);

        TipoDocumental td2 = new TipoDocumental();
        td2.setCodigo("CMP");
        td2.setNombre("Comprobante de pago");
        td2.setObligatoria(true);

        // assemble relationships using helper methods to keep both sides in sync
        ss.addTipo(td1);
        ss.addTipo(td2);

        s.addSubserie(ss);

        t.addSerie(s);

        // save root (cascade should persist the rest)
        trdRepo.save(t);
    }
}