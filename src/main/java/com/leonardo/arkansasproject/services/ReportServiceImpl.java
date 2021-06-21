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
    public Uni<Void> create(Report object) {
        return this.repository.commit(object);
    }

    @Override
    public Uni<Report> read(Long id) {
        return this.repository.read(id);
    }

    @Override
    public Uni<Report> update(Report object) {
        return this.repository.update(object);
    }

    @Override
    public Uni<Void> delete(Report object) {
        return this.repository.delete(object);
    }

    @Override
    public Uni<Report> deleteById(Long id) {
        return this.repository.deleteById(id);
    }

    @Override
    public Uni<Boolean> exists(Long id) {
        return this.repository.exists(id);
    }
}
