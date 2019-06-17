package fi.jonix.hkmyynti.commandobject;

public class LoginInfo {

	private String userName;
	private String password;
	private String redirect;
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	public String getRedirect() {
		return redirect;
	}
	
}
