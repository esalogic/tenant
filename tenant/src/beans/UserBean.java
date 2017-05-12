package beans;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@ViewScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 5006210778210985090L;
	private static final Logger log = LoggerFactory.getLogger(UserBean.class);

	private String username;
	private String tenant;
	
	/**
	 * Initialise UserBean setting <username, password> from the Session
	 */
	@PostConstruct
	public void init() {

		Session session = SecurityUtils.getSubject().getSession();
		setUsername((String) session.getAttribute("username"));
		setTenant((String) session.getAttribute("tenant"));
		log.info("Username: " + username + " tenant: " + tenant);

	}

	/**
	 * logout User and invalidate Session
	 * 
	 * @throws IOException
	 */
	public void logout() throws IOException {
		SecurityUtils.getSubject().logout();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect("login.xhtml");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

}
