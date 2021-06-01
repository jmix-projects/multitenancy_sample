package com.company.multitenancy_sample.screen.user;

import com.company.multitenancy_sample.entity.User;
import io.jmix.core.EntityStates;
import io.jmix.multitenancy.data.TenantRepository;
import io.jmix.multitenancy.entity.Tenant;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.PasswordField;
import io.jmix.ui.component.SuggestionField;
import io.jmix.ui.component.TextField;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UiController("mtensmp_User.edit")
@UiDescriptor("user-edit.xml")
@EditedEntityContainer("userDc")
@Route(value = "users/edit", parentPrefix = "users")
public class UserEdit extends StandardEditor<User> {

    @Autowired
    private EntityStates entityStates;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordField passwordField;

    @Autowired
    private TextField<String> usernameField;

    @Autowired
    private ComboBox<String> tenantIdField;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordField confirmPasswordField;

    @Autowired
    private Notifications notifications;

    @Autowired
    private MessageBundle messageBundle;

    @Subscribe
    public void onInitEntity(InitEntityEvent<User> event) {
        usernameField.setEditable(true);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);
    }

    @Subscribe
    public void onInit(InitEvent event) {
        tenantIdField.setOptionsList(tenantRepository.findAll()
                .stream()
                .map(Tenant::getTenantId)
                .collect(Collectors.toList())
        );
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        String tenantId = getEditedEntity().getTenantId();
        if (tenantId != null) {
            usernameField.setValue(getEditedEntity().getUsername().replace(tenantId+"\\",""));
        }
    }

    @Subscribe
    protected void onBeforeCommit(BeforeCommitChangesEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            if (!Objects.equals(passwordField.getValue(), confirmPasswordField.getValue())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("passwordsDoNotMatch"))
                        .show();
                event.preventCommit();
            }
            getEditedEntity().setPassword(passwordEncoder.encode(passwordField.getValue()));
        }
        User editedEntity = getEditedEntity();
        String tenantId = editedEntity.getTenantId();
        if (!Strings.isBlank(tenantId) && !editedEntity.getUsername().contains(tenantId.trim())) {
            editedEntity.setUsername(String.format("%s%s%s", tenantId, "\\", editedEntity.getUsername()));
        }
    }
}