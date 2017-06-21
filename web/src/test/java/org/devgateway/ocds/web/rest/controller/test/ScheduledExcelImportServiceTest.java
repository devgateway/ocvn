package org.devgateway.ocds.web.rest.controller.test;

import org.devgateway.ocds.web.spring.ScheduledExcelImportService;
import org.devgateway.ocds.web.spring.SendEmailService;
import org.devgateway.ocds.web.util.SettingsUtils;
import org.devgateway.toolkit.persistence.dao.AdminSettings;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author mpostelnicu
 *
 */
@ActiveProfiles("shadow-integration")
public class ScheduledExcelImportServiceTest extends AbstractWebTest {

    @Autowired
    @InjectMocks
    public ScheduledExcelImportService scheduledExcelImportService;

    @Mock
    private SettingsUtils settingsUtils;

    @Mock
    private SendEmailService sendEmailService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        AdminSettings mockSettings=new AdminSettings();
        mockSettings.setAdminEmail("mpostelnicu@developmentgateway.org");
        mockSettings.setEnableDailyAutomatedImport(true);
        mockSettings.setImportFilesPath("/test");
        Mockito.when(settingsUtils.getSettings()).thenReturn(mockSettings);
    }

    @Test
    public void testScheduledExcelImportService() {
        scheduledExcelImportService.excelImportService();

    }



}
