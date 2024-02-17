//package com.abs.wfs.lvs.util;
//
//import org.hibernate.HibernateException;
//import org.hibernate.engine.spi.SharedSessionContractImplementor;
//import org.hibernate.id.IdentifierGenerator;
//
//import java.io.Serializable;
//import java.util.UUID;
//
//public class ObjIdGenerator implements IdentifierGenerator {
//    @Override
//    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
//        String keyValueString = null;
//        UUID uuid = UUID.randomUUID();
//        String randomUuidString = uuid.toString();
//        keyValueString = CommonDate.getCurrentDateTimeToString() + "-" + randomUuidString;
//        return keyValueString;
//    }
//}
