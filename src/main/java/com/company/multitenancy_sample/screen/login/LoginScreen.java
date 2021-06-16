package com.company.multitenancy_sample.screen.login;

import io.jmix.core.CoreProperties;
import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.multitenancy.MultitenancyProperties;
import io.jmix.multitenancy.core.TenantEntityOperation;
import io.jmix.securityui.authentication.AuthDetails;
import io.jmix.securityui.authentication.LoginScreenSupport;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.CheckBox;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.PasswordField;
import io.jmix.ui.component.TextField;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.navigation.UrlRouting;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import io.jmix.ui.security.UiLoginProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.util.Locale;
import java.util.Map;

@UiController("mtensmp_LoginScreen")
@UiDescriptor("login-screen.xml")
@Route(path = "login", root = true)
public class LoginScreen extends Screen {

    @Autowired
    private TextField<String> usernameField;

    @Autowired
    private PasswordField passwordField;

    @Autowired
    private CheckBox rememberMeCheckBox;

    @Autowired
    private ComboBox<Locale> localesField;

    @Autowired
    private Notifications notifications;

    @Autowired
    private Messages messages;

    @Autowired
    private MessageTools messageTools;

    @Autowired
    private LoginScreenSupport authenticationSupport;

    @Autowired
    private CoreProperties coreProperties;

    @Autowired
    private UiLoginProperties loginProperties;

    @Autowired
    private MultitenancyProperties multitenancyProperties;

    @Autowired
    private TenantEntityOperation tenantEntityOperation;

    @Autowired
    private UrlRouting urlRouting;

    @Subscribe
    private void onInit(InitEvent event) {
        usernameField.focus();
        initLocalesField();
        initDefaultCredentials();
    }

    private void initLocalesField() {
        localesField.setOptionsMap(messageTools.getAvailableLocalesMap());
        localesField.setValue(coreProperties.getAvailableLocales().get(0));
    }

    private void initDefaultCredentials() {
        String defaultUsername = loginProperties.getDefaultUsername();
        if (!StringUtils.isBlank(defaultUsername) && !"<disabled>".equals(defaultUsername)) {
            usernameField.setValue(defaultUsername);
        } else {
            usernameField.setValue("");
        }

        String defaultPassword = loginProperties.getDefaultPassword();
        if (!StringUtils.isBlank(defaultPassword) && !"<disabled>".equals(defaultPassword)) {
            passwordField.setValue(defaultPassword);
        } else {
            passwordField.setValue("");
        }
    }

    @Subscribe("submit")
    private void onSubmitActionPerformed(Action.ActionPerformedEvent event) {
        login();
    }

    private void login() {
        String username = usernameField.getValue();
        String password = passwordField.getValue() != null ? passwordField.getValue() : "";

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messages.getMessage(getClass(), "emptyUsernameOrPassword"))
                    .show();
            return;
        }

        String tenantId = null;
        Map<String, String> params = urlRouting.getState().getParams();
        if (multitenancyProperties.isAuthenticationByTenantParamEnabled() && params != null) {
            tenantId = params.get(multitenancyProperties.getTenantIdUrlParamName());
        }
        if (tenantId != null) {
            username = String.format("%s%s%s", tenantId, "\\", username);
        }

        try {
            authenticationSupport.authenticate(
                    AuthDetails.of(username, password)
                            .withLocale(localesField.getValue())
                            .withRememberMe(rememberMeCheckBox.isChecked()), this);
        } catch (BadCredentialsException | DisabledException | LockedException e) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(messages.getMessage(getClass(), "loginFailed"))
                    .withDescription(e.getMessage())
                    .show();
        }
    }
}
