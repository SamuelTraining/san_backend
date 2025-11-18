package com.app.gestiondocumental.service; // <-- 1. PAQUETE CORREGIDO

// --- 2. IMPORTS CORREGIDOS ---
import com.app.gestiondocumental.dto.*; // Asumiendo que tus DTOs están/estarán aquí
import com.app.gestiondocumental.model.*;
import com.app.gestiondocumental.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
public class TrdService {
    // Todos estos repositorios ahora se importan correctamente
    private final TrdVersionRepository trdRepo;
    private final SerieRepository serieRepo;
    private final SubserieRepository subserieRepo;
    private final TipoDocumentalRepository tipoRepo;

    public TrdService(TrdVersionRepository trdRepo, SerieRepository serieRepo,
                      SubserieRepository subserieRepo, TipoDocumentalRepository tipoRepo) {
        this.trdRepo = trdRepo;
        this.serieRepo = serieRepo;
        this.subserieRepo = subserieRepo;
        this.tipoRepo = tipoRepo;
    }

    @Transactional(readOnly = true)
    public List<TrdDto> listTrds(String q) {
        List<TrdVersion> list = (q == null || q.isBlank()) ? trdRepo.findAll() : trdRepo.searchByName(q);
        return list.stream().map(this::toDtoOverview).collect(Collectors.toList());
    }

    private TrdDto toDtoOverview(TrdVersion t) {
        TrdDto dto = new TrdDto();
        dto.id = t.getId(); // Ahora encuentra .getId()
        dto.nombre = t.getNombre();
        dto.vigenciaDesde = t.getVigenciaDesde();
        dto.vigenciaHasta = t.getVigenciaHasta();
        dto.estado = t.getEstado();
        // series omitted in overview (client may request detail)
        return dto;
    }

    @Transactional(readOnly = true)
    // --- 3. TIPO DE ID CORREGIDO a Long ---
    public Optional<TrdDto> getTrdDetail(Long id) {
        if (id == null) return Optional.empty();
        return trdRepo.findById(id).map(t -> {
            TrdDto dto = new TrdDto();
            dto.id = t.getId();
            dto.nombre = t.getNombre();
            dto.vigenciaDesde = t.getVigenciaDesde();
            dto.vigenciaHasta = t.getVigenciaHasta();
            dto.estado = t.getEstado();

            // Asumiendo que SerieRepository fue corregido para usar Long
            List<Serie> series = serieRepo.findByTrdVersionId(t.getId());
            dto.series = (series == null ? Collections.emptyList() :
                    series.stream().map(s -> {
                        SerieDto sd = new SerieDto();
                        sd.id = s.getId();
                        sd.codigo = s.getCodigo();
                        sd.nombre = s.getNombre();

                        // Asumiendo que SubserieRepository será corregido para usar Long
                        List<Subserie> subs = subserieRepo.findBySerieId(s.getId());
                        sd.subseries = (subs == null ? Collections.emptyList() :
                                subs.stream().map(sub -> {
                                    SubserieDto subd = new SubserieDto();
                                    subd.id = sub.getId();
                                    subd.codigo = sub.getCodigo();
                                    subd.nombre = sub.getNombre();

                                    // Asumiendo que TipoDocumentalRepository será corregido para usar Long
                                    List<TipoDocumental> tipos = tipoRepo.findBySubserieId(sub.getId());
                                    subd.tipos = (tipos == null ? Collections.emptyList() :
                                            tipos.stream().map(ti -> {
                                                TipoDto td = new TipoDto();
                                                td.id = ti.getId();
                                                td.codigo = ti.getCodigo();
                                                td.nombre = ti.getNombre();
                                                td.obligatoria = ti.getObligatoria();
                                                return td;
                                            }).collect(Collectors.toList())
                                    );

                                    return subd;
                                }).collect(Collectors.toList())
                        );

                        return sd;
                    }).collect(Collectors.toList())
            );

            return dto;
        });
    }

    @Transactional(readOnly = true)
    // --- 3. TIPO DE ID CORREGIDO a Long ---
    public List<SearchResultItem> searchWithinTrd(Long trdId, String q) {
        if (trdId == null) return Collections.emptyList();

        Optional<TrdDto> opt = getTrdDetail(trdId);
        if (opt.isEmpty()) return Collections.emptyList();
        TrdDto trd = opt.get();

        String ql = (q == null) ? "" : q.trim().toLowerCase();

        return (trd.series == null ? Collections.<SearchResultItem>emptyList() :
                trd.series.stream().flatMap(s -> {
                    List<SubserieDto> subs = s.subseries == null ? Collections.emptyList() : s.subseries;
                    return subs.stream().flatMap(ss -> {
                        List<TipoDto> tipos = ss.tipos == null ? Collections.emptyList() : ss.tipos;
                        return tipos.stream()
                                .filter(t -> {
                                    String c = t.codigo == null ? "" : t.codigo.toLowerCase();
                                    String n = t.nombre == null ? "" : t.nombre.toLowerCase();
                                    return c.contains(ql) || n.contains(ql);
                                })
                                .map(t -> new SearchResultItem(
                                        s.codigo, s.nombre,
                                        ss.codigo, ss.nombre,
                                        t.codigo, t.nombre));
                    });
                }).collect(Collectors.toList())
        );
    }

    // --- Clase interna (sin cambios) ---
    public static class SearchResultItem {
        public String serieCodigo;
        public String serieNombre;
        public String subserieCodigo;
        public String subserieNombre;
        public String tipoCodigo;
        public String tipoNombre;

        public SearchResultItem(String sc, String sn, String ssc, String ssn, String tc, String tn) {
            this.serieCodigo = sc;
            this.serieNombre = sn;
            this.subserieCodigo = ssc;
            this.subserieNombre = ssn;
            this.tipoCodigo = tc;
            this.tipoNombre = tn;
        }
    }
}