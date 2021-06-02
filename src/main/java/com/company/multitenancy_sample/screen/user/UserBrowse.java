package com.company.multitenancy_sample.screen.user;

import com.company.multitenancy_sample.entity.User;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.Table;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("mtensmp_User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {
    @Autowired
    private GroupTable<User> usersTable;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Table.Column<User> username = usersTable.getColumn("username");
        if (username == null) {
            return;
        }
        username.setValueProvider(user -> {
            String tenantId = user.getTenantAttribute();
            if (tenantId != null) {
                return user.getUsername().replace(tenantId+"\\","");
            }
            return user.getUsername();
        });
    }
}