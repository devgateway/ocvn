package org.devgateway.ocds.persistence.mongo.spring;

import java.util.List;

/**
 * @author idobre
 * @since 5/20/16
 *
 * Service that imports Excel sheets in OCDS format
 */
public interface ExcelImportService extends ImportService {
    void newMsgBuffer();

    StringBuffer getMsgBuffer();

    void importAllSheets(final List<String> fileTypes, final byte[] prototypeDatabase, final byte[] locations,
                                final byte[] publicInstitutionsSuppliers,
                                final Boolean purgeDatabase, final Boolean validateData) throws InterruptedException;
}
