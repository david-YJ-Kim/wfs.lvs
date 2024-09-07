package com.abs.wfs.lvs.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ObjIdGenerator implements IdentifierGenerator {

    /** Milliseconds String Format */
    public static final String formattedMilDateString = "yyyyMMddHHmmssSSS";


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        LocalDateTime curDateTime = LocalDateTime.now();

        return (curDateTime.format(DateTimeFormatter.ofPattern(formattedMilDateString)).substring(6) + UUID.randomUUID().toString()).replaceAll("-", "");

    }
}
