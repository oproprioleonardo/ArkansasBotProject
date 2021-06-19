package com.leonardo.arkansasproject.repositories;

import com.google.inject.Singleton;
import com.leonardo.arkansasproject.database.JpaRepository;
import com.leonardo.arkansasproject.models.Report;

@Singleton
public class ReportRepositoryImpl extends JpaRepository<Report, Long> implements ReportRepository {

    public ReportRepositoryImpl() {
        super(Report.class);
    }
}
