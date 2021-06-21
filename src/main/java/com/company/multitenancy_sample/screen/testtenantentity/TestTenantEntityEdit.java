package com.company.multitenancy_sample.screen.testtenantentity;

import com.company.multitenancy_sample.entity.TestTenantEntity;
import io.jmix.ui.screen.EditedEntityContainer;
import io.jmix.ui.screen.StandardEditor;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;

@UiController("mtensmp_TestTenantEntity.edit")
@UiDescriptor("test-tenant-entity-edit.xml")
@EditedEntityContainer("testTenantEntityDc")
public class TestTenantEntityEdit extends StandardEditor<TestTenantEntity> {
}