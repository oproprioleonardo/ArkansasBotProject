package com.leonardo.arkansasproject.repositories.impl;

import com.google.inject.Singleton;
import com.leonardo.arkansasproject.database.JpaRepository;
import com.leonardo.arkansasproject.entities.Report;
import com.leonardo.arkansasproject.repositories.ReportRepository;

@Singleton
public class ReportRepositoryImpl extends JpaRepository<Report, Long> implements ReportRepository {

    public ReportRepositoryImpl() {
        super(Report.class);
    }
}
