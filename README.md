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

4. Add following code in the end onBeforeCommit handler from UserEdit class

```java
    User editedEntity=getEditedEntity();
    String tenantId=editedEntity.getTenantId();
    if(!Strings.isBlank(tenantId)&&!editedEntity.getUsername().contains(tenantId.trim())){
        editedEntity.setUsername(String.format("%s%s%s",tenantId,"\\",editedEntity.getUsername()));
    }
```

5. Add method in UserEdit class

```java
    @Subscribe
    public void onAfterShow(AfterShowEvent event){
        String tenantId=getEditedEntity().getTenantId();
        if(tenantId!=null){
            usernameField.setValue(getEditedEntity().getUsername().replace(tenantId+"\\",""));
        }
    }
```

6. Add following code in UserBrowse class

```java
    @Autowired
    private GroupTable<User> usersTable;
    @Subscribe
    public void onBeforeShow(BeforeShowEvent event){
        Table.Column<User> username=usersTable.getColumn("username");
        if(username==null){
            return;
        }
        username.setValueProvider(user->{
            String tenantId=user.getTenantId();
            if(tenantId!=null){
                return user.getUsername().replace(tenantId+"\\","");
            }
            return user.getUsername();
        });
    }
```

7. Add following code in LoginScreen class

```java
    @Autowired
    private MultitenancyProperties multitenancyProperties;

    @Autowired
    private UrlRouting urlRouting;
```

8. Add code into 'login' method from LoginScreen class before try-catch block

```java
    String tenantId=null;
    Map<String, String> params=urlRouting.getState().getParams();
    if(multitenancyProperties.isAuthenticationByTenantParamEnabled()&&params!=null){
        tenantId=params.get(multitenancyProperties.getTenantIdUrlParamName());
    }
    if(tenantId!=null){
        username=String.format("%s%s%s",tenantId,"\\",username);
    }
```

9. Add combobox for tenant in user-edit.xml

```xml
    <comboBox id="tenantIdField" property="tenantAttribute"/>
```

10. Add code in UserEdit class

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

11. Add column into table in user-browse.xml

```xml
    <column id="tenantAttribute"/>
```

12. Add code in UserBrowse class

```java
    @Autowired
    private GroupTable<User> usersTable;
    @Subscribe
    public void onBeforeShow(BeforeShowEvent event){
        Table.Column<User> username=usersTable.getColumn("username");
        if(username==null){
            return;
        }
        username.setValueProvider(user->{
           String tenantId=user.getTenantId();
           if(tenantId!=null){
               return user.getUsername().replace(tenantId+"\\","");
           }
          return user.getUsername();
        });
    }
```

Now you can run your application, create tenant-specific entities, create roles for the entities, assignment roles for
users and other.