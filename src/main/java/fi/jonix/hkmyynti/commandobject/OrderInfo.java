package fi.jonix.hkmyynti.commandobject;

public class OrderInfo {

	private String firstName;
	private String lastName;
	private String address;
	private String postCode;
	private String city;
	private String emailAddress;
	private String marketSalesId;
	private String referenceNumber;
	private int amount;
	private String confirmationCode;
	private String accountNumber;
	/*private String adTemplateHeadline; //TODO: remove this one
	private String adUrl; //TODO: remove this one
	private String imageUrl; //TODO: remove this one
	*/
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return city;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setMarketSalesId(String marketSalesId) {
		this.marketSalesId = marketSalesId;
	}
	public String getMarketSalesId() {
		return marketSalesId;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getAmount() {
		return amount;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}
	public String getConfirmationCode() {
		return confirmationCode;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	/*public void setAdTemplateHeadline(String adTemplateHeadline) {
		this.adTemplateHeadline = adTemplateHeadline;
	}
	public String getAdTemplateHeadline() {
		return adTemplateHeadline;
	}
	public void setAdUrl(String adUrl) {
		this.adUrl = adUrl;
	}
	public String getAdUrl() {
		return adUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	*/
	
}
