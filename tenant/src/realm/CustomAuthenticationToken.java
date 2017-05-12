package realm;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

public class CustomAuthenticationToken implements HostAuthenticationToken, RememberMeAuthenticationToken {
	/*--------------------------------------------
	|             C O N S T A N T S             |
	============================================*/

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*--------------------------------------------
	|    I N S T A N C E   V A R I A B L E S    |
	============================================*/
	/**
	 * The username
	 */
	private String username;

	/**
	 * The tenant
	 */
	private String tenant;

	/**
	 * The password, in char[] format
	 */
	private char[] password;

	/**
	 * Whether or not 'rememberMe' should be enabled for the corresponding login
	 * attempt; default is <code>false</code>
	 */
	private boolean rememberMe = false;

	/**
	 * The location from where the login attempt occurs, or <code>null</code> if
	 * not known or explicitly omitted.
	 */
	private String host;

	/*--------------------------------------------
	|         C O N S T R U C T O R S           |
	============================================*/

	/**
	 * JavaBeans compatible no-arg constructor.
	 */
	public CustomAuthenticationToken() {
	}

	/**
	 * Constructs a new CustomAuthenticationToken encapsulating the username and
	 * password submitted during an authentication attempt, with a <tt>null</tt>
	 * {@link #getHost() host} and a <tt>rememberMe</tt> default of
	 * <tt>false</tt>.
	 *
	 * @param username
	 *            the username submitted for authentication
	 * @param password
	 *            the password character array submitted for authentication
	 */
	public CustomAuthenticationToken(final String username, final char[] password) {
		this(username, password, false, null);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 */
	public CustomAuthenticationToken(final String username, final String password) {
		this(username, password != null ? password.toCharArray() : null, false, null);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param host
	 */
	public CustomAuthenticationToken(final String username, final char[] password, final String host) {
		this(username, password, false, host);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param host
	 */
	public CustomAuthenticationToken(final String username, final String password, final String host) {
		this(username, password != null ? password.toCharArray() : null, false, host);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param rememberMe
	 */
	public CustomAuthenticationToken(final String username, final char[] password, final boolean rememberMe) {
		this(username, password, rememberMe, null, null);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param rememberMe
	 */
	public CustomAuthenticationToken(final String username, final String password, final boolean rememberMe) {
		this(username, password != null ? password.toCharArray() : null, rememberMe, null, null);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param rememberMe
	 * @param host
	 */
	public CustomAuthenticationToken(final String username, final char[] password, final boolean rememberMe,
			final String host) {

		this(username, password, rememberMe, host, null);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param rememberMe
	 * @param host
	 */
	public CustomAuthenticationToken(final String username, final String password, final boolean rememberMe,
			final String host) {
		this(username, password != null ? password.toCharArray() : null, rememberMe, host, null);
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param rememberMe
	 * @param host
	 * @param tenant
	 */
	public CustomAuthenticationToken(final String username, final char[] password, final boolean rememberMe,
			final String host, final String tenant) {

		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
		this.host = host;
		this.tenant = tenant;
	}

	/**
	 * 
	 * Constructs a new CustomAuthenticationToken encapsulating the username, password
	 * and tenant submitted, if the user wishes their identity to be remembered
	 * across sessions, and the inetAddress from where the attempt is occurring.
	 * <p/>
	 * <p>
	 * This is a convenience constructor and maintains the password internally
	 * via a character array, i.e. <tt>password.toCharArray();</tt>. Note that
	 * storing a password as a String in your code could have possible security
	 * implications as noted in the class JavaDoc.
	 * </p>
	 * @param username
	 * @param password
	 * @param rememberMe
	 * @param host
	 * @param tenant
	 */
	public CustomAuthenticationToken(final String username, final String password, final boolean rememberMe,
			final String host, final String tenant) {
		this(username, password != null ? password.toCharArray() : null, rememberMe, host, tenant);
	}

	/*--------------------------------------------
	|  A C C E S S O R S / M O D I F I E R S    |
	============================================*/

	/**
	 * Returns the username submitted during an authentication attempt.
	 *
	 * @return the username submitted during an authentication attempt.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username for submission during an authentication attempt.
	 *
	 * @param username
	 *            the username to be used for submission during an
	 *            authentication attempt.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the password submitted during an authentication attempt as a
	 * character array.
	 *
	 * @return the password submitted during an authentication attempt as a
	 *         character array.
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * Sets the password for submission during an authentication attempt.
	 *
	 * @param password
	 *            the password to be used for submission during an
	 *            authentication attempt.
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}

	/**
	 * Simply returns {@link #getUsername() getUsername()}.
	 *
	 * @return the {@link #getUsername() username}.
	 * @see org.apache.shiro.authc.AuthenticationToken#getPrincipal()
	 */
	public Object getPrincipal() {
		return getUsername();
	}

	/**
	 * Returns the {@link #getPassword() password} char array.
	 *
	 * @return the {@link #getPassword() password} char array.
	 * @see org.apache.shiro.authc.AuthenticationToken#getCredentials()
	 */
	public Object getCredentials() {
		return getPassword();
	}

	/**
	 * Returns the host name or IP string from where the authentication attempt
	 * occurs. May be <tt>null</tt> if the host name/IP is unknown or explicitly
	 * omitted. It is up to the Authenticator implementation processing this
	 * token if an authentication attempt without a host is valid or not.
	 * <p/>
	 * <p>
	 * (Shiro's default Authenticator allows <tt>null</tt> hosts to support
	 * localhost and proxy server environments).
	 * </p>
	 *
	 * @return the host from where the authentication attempt occurs, or
	 *         <tt>null</tt> if it is unknown or explicitly omitted.
	 * @since 1.0
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host name or IP string from where the authentication attempt
	 * occurs. It is up to the Authenticator implementation processing this
	 * token if an authentication attempt without a host is valid or not.
	 * <p/>
	 * <p>
	 * (Shiro's default Authenticator allows <tt>null</tt> hosts to allow
	 * localhost and proxy server environments).
	 * </p>
	 *
	 * @param host
	 *            the host name or IP string from where the attempt is occurring
	 * @since 1.0
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns <tt>true</tt> if the submitting user wishes their identity
	 * (principal(s)) to be remembered across sessions, <tt>false</tt>
	 * otherwise. Unless overridden, this value is <tt>false</tt> by default.
	 *
	 * @return <tt>true</tt> if the submitting user wishes their identity
	 *         (principal(s)) to be remembered across sessions, <tt>false</tt>
	 *         otherwise (<tt>false</tt> by default).
	 * @since 0.9
	 */
	public boolean isRememberMe() {
		return rememberMe;
	}

	/**
	 * Sets if the submitting user wishes their identity (principal(s)) to be
	 * remembered across sessions. Unless overridden, the default value is
	 * <tt>false</tt>, indicating <em>not</em> to be remembered across sessions.
	 *
	 * @param rememberMe
	 *            value indicating if the user wishes their identity
	 *            (principal(s)) to be remembered across sessions.
	 * @since 0.9
	 */
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	/*--------------------------------------------
	|               M E T H O D S               |
	============================================*/

	/**
	 * Clears out (nulls) the username, password, rememberMe, and inetAddress.
	 * The password bytes are explicitly set to <tt>0x00</tt> before nulling to
	 * eliminate the possibility of memory access at a later time.
	 */
	public void clear() {
		this.username = null;
		this.host = null;
		this.rememberMe = false;
		this.tenant = null;

		if (this.password != null) {
			for (int i = 0; i < password.length; i++) {
				this.password[i] = 0x00;
			}
			this.password = null;
		}

	}

	/**
	 * Returns the String representation. It does not include the password in
	 * the resulting string for security reasons to prevent accidentally
	 * printing out a password that might be widely viewable).
	 *
	 * @return the String representation of the
	 *         <tt>CustomAuthenticationToken</tt>, omitting the password.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(" - ");
		sb.append(username);
		sb.append(" - ");
		sb.append(tenant);
		sb.append(", rememberMe=").append(rememberMe);
		if (host != null) {
			sb.append(" (").append(host).append(")");
		}
		return sb.toString();
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

}
