# Multitenancy sample project

This is a sample multitenancy project. It creates from a single-module Jmix project if you want to create the same
project you can use the following instructions.

Steps for getting multitenancy project from empty Jmix project:

1. Add multitenancy-starter and multitenancy-ui-starter in build.gradle

```groovy
    implementation 'io.jmix.multitenancy:jmix-multitenancy-starter'
    implementation 'io.jmix.multitenancy:jmix-multitenancy-ui-starter'
```

2. Add attribute in UserEntity which will be tenant Id, it must have String type and annotation TenantId. For example

```java
    @TenantId
    @Column(name = "TENANT_ATTRIBUTE")
    protected String tenantAttribute;
```

3. Implement interface TenantSupport in UserEntity. Method implementation from interface must return value of attribute
   marked TenantId annotation

```java
    @Override
    public String getTenantId(){
        return tenantAttribute;
    }
```

4. Add method in UserEdit class

```java
    @Subscribe("tenantIdField")
    public void onTenantIdFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        usernameField.setValue(multitenancyUsernameSupport.getMultitenancyUsername(usernameField.getValue(), event.getValue()));
    }
```

5. Add following code in LoginScreen class

```java
    @Autowired
    private MultitenancyUsernameSupport multitenancyUsernameSupport;

    @Autowired
    private UrlRouting urlRouting;
```

6. Add code into 'login' method from LoginScreen class before try-catch block

```java
    username = multitenancyUsernameSupport.getMultitenancyUsername(username, urlRouting.getState().getParams());
```

7. Add combobox for tenant in user-edit.xml

```xml
    <comboBox id="tenantIdField" property="tenantAttribute"/>
```

8. Add code in UserEdit class

```java
    @Autowired
    private ComboBox<String> tenantIdField;
    @Autowired
    private DataManager dataManager;
    @Subscribe
    public void onInit(InitEvent event){
        tenantIdField.setOptionsList(dataManager.load(Tenant.class)
        .query("select t from mten_Tenant t")
        .list()
        .stream()
        .map(Tenant::getTenantId)
        .collect(Collectors.toList()));
    }
```

9. Add column into table in user-browse.xml

```xml
    <column id="tenantAttribute"/>
```

Now you can run your application, create tenant-specific entities, create roles for the entities, assignment roles for
users and other.