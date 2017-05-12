package beans;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.superbiz.dynamicdatasourcerouting.RoutedPersister;

@ManagedBean
@RequestScoped
public class InsertUserBean {

	@Inject
	private RoutedPersister router;

	private String username;
	private String password;
	private String user_role;

	private static final Logger logger = Logger.getLogger(RoutedPersister.class);
	
	/**
	 * Add a new User in users <tenant, table>
	 */
	public void save() {
		logger.info("Adding new User: " + username);
		try {
			router.persist(username, new Sha256Hash(password).toHex(), user_role);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "User added", null));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "User not added", null));
		}
	}
	
	/**
	 * logout User and invalidate Session
	 * 
	 * @throws IOException
	 */
	public void logout() throws IOException {
		logger.info("Logging out...");
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser_role() {
		return user_role;
	}

	public void setUser_role(String user_role) {
		this.user_role = user_role;
	}

}
