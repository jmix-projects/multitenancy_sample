package com.company.multitenancy_sample.security;

import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

@ResourceRole(name = "Test Tenant Access", code = TestTenantAccess.CODE)
public interface TestTenantAccess {

    String CODE = "test-tenant-access";

    @EntityPolicy(entityName = "mtensmp_TestTenantEntity", actions = {EntityPolicyAction.ALL})
    @EntityAttributePolicy(entityName = "mtensmp_TestTenantEntity", attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @ScreenPolicy(screenIds = {"mtensmp_TestTenantEntity.browse", "mtensmp_TestTenantEntity.edit"})
    @MenuPolicy(menuIds = {"mtensmp_TestTenantEntity.browse"})
    void testTenantAccess();

}
