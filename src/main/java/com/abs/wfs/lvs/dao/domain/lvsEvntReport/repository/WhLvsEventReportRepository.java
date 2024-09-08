package com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository;

import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WhLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.util.vo.UseStatCd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhLvsEventReportRepository extends JpaRepository<WhLvsEventReport, String> {
}
