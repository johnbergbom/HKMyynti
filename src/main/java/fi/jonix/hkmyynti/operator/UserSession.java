package fi.jonix.hkmyynti.operator;

import java.util.Date;

import fi.jonix.huutonet.domain.model.User;

public class UserSession {
	
	private Date startTime;
	private User user;

	public UserSession(Date startTime, User user) {
		this.startTime = startTime;
		this.user = user;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
	
}
