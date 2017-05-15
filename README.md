# Tenant

Our goal is to implement multi-tenancy as software architecture.
We decided to manage one tenant per DB with separate connections to emprove security access.

## Getting Started

To test the project you need:
  Java JDK
  Java JRE
  Eclipse Neon
  Apache MyFaces
  OpenJPA
  apache-tomee-plus-7.0.2
  pgAdmin4 & PostgreSQL

### Installing

Open Eclipse and set apache-tomee-plus-7.0.2 as Tomacat v8.5 Server in Project->Properties->Server option.
Create a new Dynamic Web Project and replace "src" folder in Java Resources and "WebContent" folder in root directory with tenant's one.
First thing you need to do is to configure your environement (JAVA JDK,JRE and Eclipse). 
Afther that, you have to open pgAdmin and create two new DB called: tenant_a, tenant_b. For each of them execute query contained in query.sql in JavaResources->src->resources.

query.sql

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

```

Just replace "tenant_a" with "tenant_b" when u insert users in different DB.

Now that you have configured two differents DB you need to modify configuration files.
First of all you need to modify web.xml and resources.xml to match your DB configuration (check your postgreSQL port). 

web.xml
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

resources.xml
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
As you can see we implemented our DataSource access as Routed_Datasource. We istantiate one Router per tenant and dynamically match DataSource by name.

DeterminedRouter.java
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

The core of our architecture is to give the chance to match users in different databases using Shiro security framework. To do this we needed to access with a Triple <Username, Password, Tenant_Name>. To do this we had to bypass custom Shiro's Realm-Token protocol to match our one.
First of all we extended CustomAuthenticationToken to match our Triple: you can see the constructor. 

CustomAuthenticationToken.java
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

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
