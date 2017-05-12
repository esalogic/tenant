/**
 * Very simple bean that authenticates the user via Apache Shiro, using JSF
 * @author Daniel Mascarenhas
 */
package shiro.bean.security;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import realm.CustomAuthenticationToken;

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

		//
	}

	/**
	 * Adds a new SEVERITY_ERROR FacesMessage for the ui
	 * 
	 * @param message
	 *            Error Message
	 */
	private void facesError(String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String login) {
		this.username = login;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String senha) {
		this.password = senha;
	}

	public Boolean getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(Boolean lembrar) {
		this.rememberMe = lembrar;
	}
}
