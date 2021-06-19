package com.leonardo.arkansasproject.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.repositories.ReportRepository;
import io.smallrye.mutiny.Uni;

@Singleton
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportRepository repository;

    @Override
    public Uni<Report> create(Report object) {
        return null;
    }

    @Override
    public Uni<Report> read(Long id) {
        return null;
    }

    @Override
    public Uni<Report> update(Report object) {
        return null;
    }

    @Override
    public Uni<Void> delete(Report object) {
        return null;
    }

    @Override
    public Uni<Report> deleteById(Long id) {
        return null;
    }

    @Override
    public Uni<Boolean> exists(Long id) {
        return null;
    }
}
