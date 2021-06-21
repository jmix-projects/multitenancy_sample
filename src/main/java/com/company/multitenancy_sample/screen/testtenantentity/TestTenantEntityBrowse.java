package com.company.multitenancy_sample.screen.testtenantentity;

import com.company.multitenancy_sample.entity.TestTenantEntity;
import io.jmix.ui.screen.LookupComponent;
import io.jmix.ui.screen.StandardLookup;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;

@UiController("mtensmp_TestTenantEntity.browse")
@UiDescriptor("test-tenant-entity-browse.xml")
@LookupComponent("testTenantEntitiesTable")
public class TestTenantEntityBrowse extends StandardLookup<TestTenantEntity> {
}