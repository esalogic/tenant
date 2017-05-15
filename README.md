# Tenant

Our goal is to implement multi-tenancy using **EJB + Shiro + JPA + PostgreSQL**.  
Our first intention was to implement multi-tenancy associating for each tenant a different DB-schema placed in a single DB: we wanted to access from a single DB-table, containing _<User,Password,Tenant>_, different DB-schema's in same DB.  
This method should have simplified our management protocol to generate different DB-schema's per tenant allowing to manage all the features with a single connection.  
We were not able to successfully introduce this pattern because openJPA doesn't easily permit to dynamically create and manage DBschema's.  

We decided to manage one tenant per DB where each DB contains a table with _<User,Password,Tenant>_ association.  
We establish one different connection per DB.  
We implemented [Dynamic datasource routing](http://tomee.apache.org/examples/dynamic-datasource-routing/README.html) to manage every interaction with a specific DB. This solution has some benefits but forced us to know and declare all the components we need to use before starting the application.

**Tenant per DB pattern _Pros & Cons_:**

**Pros:**
* separate connections
* security access
* separation of data  

**Cons:**
* static resource declaration
* openJPA doesn't autonomously create DB-table


## Getting Started

To test the project you need:
* [Java JDK and Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Eclipse Neon](http://www.eclipse.org/neon/)
* [Apache My Faces](https://myfaces.apache.org/)
* [OpenJPA](http://openjpa.apache.org/)
* [apache-tomee-plus-7.0.2](http://openejb.apache.org/apache-tomee.html)
* [pgAdmin4](https://www.pgadmin.org/) and [PostgreSQL](https://www.postgresql.org/)

### Installing

Open Eclipse and set apache-tomee-plus-7.0.2 as Tomacat v8.5 Server in Project->Properties->Server option.  
Create a new Dynamic Web Project and replace "src" folder in Java Resources and "WebContent" folder in root directory with tenant's one.  
First thing you need to do is to configure your environement (JAVA JDK,JRE and Eclipse).  
After that, you have to open pgAdmin and create two new DB called: tenant_a, tenant_b.  
For each of them execute queries contained in query.sql placed in JavaResources->src->resources.  
You can insert as many users as you want to test our features.

**query.sql**

```
CREATE TABLE public.users
(
    username character varying(256) COLLATE pg_catalog."default" NOT NULL,
    password character varying COLLATE pg_catalog."default" NOT NULL,
    password_salt character varying(256) COLLATE pg_catalog."default",
    tenant character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (username)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.users
    OWNER to tenant;

ALTER TABLE public.users
    OWNER to tenant;

-- Table: public.user_roles

-- DROP TABLE public.user_roles;

CREATE TABLE public.user_roles
(
    username character varying(256) COLLATE pg_catalog."default",
    role_name character varying(256) COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.user_roles
    OWNER to tenant;

-- Table: public.roles_permissions

-- DROP TABLE public.roles_permissions;

CREATE TABLE public.roles_permissions
(
    role_name character varying(256) COLLATE pg_catalog."default",
    permission character varying(256) COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.roles_permissions
    OWNER to tenant;


INSERT INTO public.users(
username, password, password_salt, tenant)
VALUES ('user_1', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 'tmp', 'tenant_a');
`
```

Just replace "tenant_a" with "tenant_b" when u insert users in different DB's.  

Now that you have configured two differents DB's you need to modify configuration files.  
First of all you need to modify web.xml and resources.xml to match your DB's configuration (check your postgreSQL port). 

**web.xml**
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://xmlns.jcp.org/xml/ns/javaee"
	xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
	id="WebApp_ID" version="3.1">
	<display-name>TENANT_ROUTER</display-name>
	
	<welcome-file-list>
		<welcome-file>login.xhtml</welcome-file>
	</welcome-file-list>
	
	<servlet>
		<servlet-name>Faces Servlet</servlet-name>
		<servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	
	<servlet-mapping>
		<servlet-name>Faces Servlet</servlet-name>
		<url-pattern>*.xhtml</url-pattern>
	</servlet-mapping>

	<listener>
		<listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
	</listener>
	<filter>
		<filter-name>ShiroFilter</filter-name>
		<filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>ShiroFilter</filter-name>
		<url-pattern>/*</url-pattern>
		<dispatcher>REQUEST</dispatcher>
		<dispatcher>FORWARD</dispatcher>
		<dispatcher>INCLUDE</dispatcher>
		<dispatcher>ERROR</dispatcher>
	</filter-mapping>

	<data-source>
		<name>java:app/tenant_b</name>
		<class-name>org.postgresql.Driver</class-name>
		<url>jdbc:postgresql://localhost:5432/tenant_b</url>
		<user>tenant</user>
		<password>tenant</password>
	</data-source>
	
	<data-source>
		<name>java:app/tenant_a</name>
		<class-name>org.postgresql.Driver</class-name>
		<url>jdbc:postgresql://localhost:5432/tenant_a</url>
		<user>tenant</user>
		<password>tenant</password>
	</data-source>

</web-app>
```


**resources.xml**
```
<?xml version="1.0" encoding="UTF-8"?>
<resources>

	<Resource id="My_Router"
		type="org.superbiz.dynamicdatasourcerouting.DeterminedRouter"
		provider="org.router:DeterminedRouter">
		DatasourceNames = tenant_a tenant_b
		DefaultDataSourceName =
		tenant_b
	</Resource>

	<Resource id="Routed_Datasource" type="org.apache.openejb.resource.jdbc.router.Router"
		provider="RoutedDataSource">
		Router = My_Router
	</Resource>

	<Resource id="tenant_b" type="DataSource">
		JdbcDriver =
		org.postgresql.Driver
		JdbcUrl =
		jdbc:postgresql://localhost:5432/tenant_b
		UserName = tenant
		Password =
		tenant
		JtaManaged = true
		maxActive=50
		maxIdle=3
		maxWait=100000
		poolPreparedStatements=true
		maxOpenPreparedStatements=100
	</Resource>

	<Resource id="tenant_a" type="DataSource">
		JdbcDriver =
		org.postgresql.Driver
		JdbcUrl =
		jdbc:postgresql://localhost:5432/tenant_a
		UserName = tenant
		Password =
		tenant
		JtaManaged = true
		maxActive=50
		maxIdle=3
		maxWait=100000
		poolPreparedStatements=true
		maxOpenPreparedStatements=100
	</Resource>

</resources>

```
As you can see we implemented our DataSource access as Routed_Datasource.  
We instantiate one Router per tenant and dynamically match DataSource by name.  
More details in [Dynamic DataSource Routing](https://github.com/apache/tomee/tree/master/examples/dynamic-datasource-routing).  

We faced another problem trying to dynamically create our DB's: 
according to [TomEE](http://tomee.apache.org/examples/dynamic-datasource-routing/README.html) documentation:
> However with OpenJPA (the default JPA provider for OpenEJB), the creation is lazy and it happens only once so when you'll switch of database it will no more work.

We decided to manually create DB's before the deployment and define them in our config files.

**DeterminedRouter.java**
```
/**
 * @return the user selected data source if it is set or the default one
 * @throws IllegalArgumentException
 *             if the data source is not found
 */
@Override
public DataSource getDataSource() {

	if (dataSources == null) {
		logger.info("Lazy DataSource load!!");
		init();
	}

	// if no datasource is selected use the default one
	if (currentDataSource.get() == null) {
		if (dataSources.containsKey(defaultDataSourceName)) {
			return dataSources.get(defaultDataSourceName);

		} else {
			throw new IllegalArgumentException("you have to specify at least one datasource");
		}
	}

	// the developper set the datasource to use
	return currentDataSource.get();
}
```

The core of our architecture is to give the chance to match users in different databases using Shiro security framework and JPA in EJB context. To do this we needed to access with a Triple <Username, Password, Tenant_Name> bypassing custom Shiro's Realm-Token protocol to fit our one.  
First of all we extended AuthenticationToken class to get Tenant_Name as parameter.

**CustomAuthenticationToken.java**
```

public class CustomAuthenticationToken implements HostAuthenticationToken, RememberMeAuthenticationToken {
  
  ...
  
	public CustomAuthenticationToken(final String username, final char[] password, final boolean rememberMe,
			final String host, final String tenant) {

		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
		this.host = host;
		this.tenant = tenant;
	}
  
  ...
  
}
```

After that we extended JDBCRealm class to permit to login with our Triple <Username, Password, Tenant>.  
We wanted to have DEFAULT query setted in the class.  
You can decide to write your own queries in shiro.ini configuration file as jdbcRealm.authenticationQuery=YOUR_QUERY and jdbcRealm.userRolesQuery=YOUR_QUERY properties.  
These are our main modifications:

**CustomJDBCRealm.java**

```

...

public class CustomJDBCRealm extends AuthorizingRealm {


/**
 * Rewrited to check <username, tenant> match
 */
protected static final String DEFAULT_AUTHENTICATION_QUERY = 
	"select password from users where username = ? and tenant = 	?";

/**
 * Rewrited to check <username, tenant> match
 */
protected static final String DEFAULT_SALTED_AUTHENTICATION_QUERY = 
	"select password, password_salt from users where username = ? and tenant = ?";

/**
 * getPasswordForUser ( -, -, - ) now gets three parameter
 */
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

	CustomAuthenticationToken upToken = (CustomAuthenticationToken) token;
	String username = upToken.getUsername();
	String password = upToken.getPassword().toString();
	String tenant = upToken.getTenant();
	// Null username is invalid
	if (username == null) {
		throw new AccountException("Null usernames are not allowed by this realm.");
	}
	if (tenant == null) {
		throw new AccountException("Null tenants are not allowed by this realm.");
	}

	Connection conn = null;
	SimpleAuthenticationInfo info = null;
	try {
		conn = dataSource.getConnection();

		String salt = null;
		switch (saltStyle) {
		case NO_SALT:
			password = getPasswordForUser(conn, username, tenant)[0];
			break;
		case CRYPT:
			// TODO: separate password and hash from getPasswordForUser[0]
			throw new ConfigurationException("Not implemented yet");
			// break;
		case COLUMN:
			String[] queryResults = getPasswordForUser(conn, username, tenant);
			password = queryResults[0];
			salt = queryResults[1];
			break;
		case EXTERNAL:
			password = getPasswordForUser(conn, username, tenant)[0];
			salt = getSaltForUser(username);
		}

		if (password == null) {
			throw new UnknownAccountException("No account found for user [" + username + "]");
		}

		info = new SimpleAuthenticationInfo(username, password.toCharArray(), getName());

		if (salt != null) {
			info.setCredentialsSalt(ByteSource.Util.bytes(salt));
		}

	} catch (SQLException e) {
		final String message = "There was a SQL error while authenticating user [" + username + "]";
		if (log.isErrorEnabled()) {
			log.error(message, e);
		}

		// Rethrow any SQL errors as an authentication exception
		throw new AuthenticationException(message, e);
	} finally {
		JdbcUtils.closeConnection(conn);
	}

	return info;
}

/**
 * 
 * Modified to now get three parameter
 * ps = conn.prepareStatement(authenticationQuery);
 * ps.setString(1, username);
 * ps.setString(2, tenant);
 * 
 * @param conn
 * @param username
 * @param tenant
 * @return
 * @throws SQLException
 */
private String[] getPasswordForUser(Connection conn, String username, String tenant) throws SQLException {

	String[] result;
	boolean returningSeparatedSalt = false;
	switch (saltStyle) {
	case NO_SALT:
	case CRYPT:
	case EXTERNAL:
		result = new String[1];
		break;
	default:
		result = new String[2];
		returningSeparatedSalt = true;
	}

	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
		ps = conn.prepareStatement(authenticationQuery);
		ps.setString(1, username);
		ps.setString(2, tenant);

		// Execute query
		rs = ps.executeQuery();

		// Loop over results - although we are only expecting one result,
		// since usernames should be unique
		boolean foundResult = false;
		while (rs.next()) {

			// Check to ensure only one row is processed
			if (foundResult) {
				throw new AuthenticationException(
						"More than one user row found for user [" + username + "]. Usernames must be unique.");
			}

			result[0] = rs.getString(1);
			if (returningSeparatedSalt) {
				result[1] = rs.getString(2);
			}

			foundResult = true;
		}
	} finally {
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeStatement(ps);
	}

	return result;
}

}


```

Now it remains to configure Shiro and implement a new login politics.  
As you can notice, the system will try to complete login procedure sequentially on each Realm. So it will return the right connection to the first DB that contains the triple <Username, Password, Tenant>.
You can see how we configure Shiro by checking shiro.ini configuration file:


**shiro.ini**
```
[main]
authc = org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter
builtInCacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager
securityManager.cacheManager = $builtInCacheManager


authc.loginUrl = /login.xhtml
authc.successUrl = /person.xhtml
roles.unauthorizedUrl = /unauthorized.xhtml

ssl.enabled = false

# Use default password matcher (SHA-256, 500000 hash iterations)
credentialsMatcher = org.apache.shiro.authc.credential.HashedCredentialsMatcher
credentialsMatcher.hashAlgorithmName = SHA-256

# DataSource Setup
datasource1 = org.apache.shiro.jndi.JndiObjectFactory
datasource1.resourceName = java:app/tenant_a

datasource2 = org.apache.shiro.jndi.JndiObjectFactory
datasource2.resourceName = java:app/tenant_b

# JDBC Realm
jdbcRealm1 = realm.CustomJDBCRealm
jdbcRealm1.credentialsMatcher = $credentialsMatcher
jdbcRealm1.dataSource = $datasource1

jdbcRealm2 = realm.CustomJDBCRealm
jdbcRealm2.credentialsMatcher = $credentialsMatcher
jdbcRealm2.dataSource = $datasource2

# Filter Setup
[urls]
/javax.faces.resource/** = anon
/login.xhtml = authc
/users.xhtml = authc, roles[admin]
/index.html = authc, roles[user]

```

We defined two different datasource named: datasource1 and datasource2.   
We defined two different jdbcRealm each one is a CustomJDBCRealm and has datasource1/2 as source.  
You can implement your own filter politics and create as many DataSource/Realms as you need.  

Last interesting thing is ShiroLoginBean.java. We fill our session with "username" and "tenant" parameter so we can correctly instantiate our backingBean (UserBean) at run time.

**ShiroLoginBean.java**
```

...

@ManagedBean
@ViewScoped
public class ShiroLoginBean implements Serializable {

private static final long serialVersionUID = 2947777521214175822L;

private static final Logger log = LoggerFactory.getLogger(ShiroLoginBean.class);

private String username;
private String tenant;
private String password;
private Boolean rememberMe;

public ShiroLoginBean() {
}

@PostConstruct
public void init() {
}

/**
 * Now authenticate the User by <Username, Password, Tenant>. Shiro will automatically generate a new EntityManager with a specific connection for each tenant
 */
public void doLogin() {

	Subject subject = SecurityUtils.getSubject();

	CustomAuthenticationToken token = new CustomAuthenticationToken(getUsername(), getPassword().toCharArray(),
			getRememberMe(), null, tenant);

	try {

		subject.login(token);

		Session session = subject.getSession();
		session.setAttribute("username", username);
		session.setAttribute("tenant", tenant);

		if (subject.hasRole("admin")) {
			FacesContext.getCurrentInstance().getExternalContext().redirect("users.xhtml");
		} else {
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		}

	} catch (UnknownAccountException ex) {
		facesError("Unknown account");
		log.error(ex.getMessage(), ex);
	} catch (IncorrectCredentialsException ex) {
		facesError("Wrong password");
		log.error(ex.getMessage(), ex);
	} catch (LockedAccountException ex) {
		facesError("Locked account");
		log.error(ex.getMessage(), ex);
	} catch (AuthenticationException | IOException ex) {
		facesError("Unknown account: ");
		log.error(ex.getMessage(), ex);
	} finally {
		token.clear();
	}

}

...


```

## Running the tests
To run a valid test you need to:
* execute tomee from terminal
* export Project as WAR file placing it in the "webapps" folder
* go to "localhost:8080/TENANT_ROUTER_SHIRO" and test login featur


## Built With

* [Apache My Faces](https://myfaces.apache.org/)
* [OpenJPA](http://openjpa.apache.org/)
* [TomEE](http://openejb.apache.org/apache-tomee.html)
* [PostgreSQL](https://www.postgresql.org/)
* [Dynamic DataSource Routing](https://github.com/apache/tomee/tree/master/examples/dynamic-datasource-routing)
* [Eclipse Neon](http://www.eclipse.org/neon/)


## Contributing

This work is an implementation of [Dynamic DataSource Routing](https://github.com/apache/tomee/tree/master/examples/dynamic-datasource-routing) to fit with multi-tenant architecture.

## Authors

* **Umberto Carrara** - [Esalogic](http://esalogic.it/) - developer
* **Vincenzo Ciaralli** - [Esalogic](http://esalogic.it/) - developer
