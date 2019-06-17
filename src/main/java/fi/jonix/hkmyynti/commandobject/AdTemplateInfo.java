package fi.jonix.hkmyynti.commandobject;

import java.util.ArrayList;
import java.util.List;

import fi.jonix.huutonet.domain.model.AdTemplate;
import fi.jonix.huutonet.domain.model.Product;

public class AdTemplateInfo {

	private long adTemplateId;
	private String headline;
	private List<String> details;
	private List<String> technicalSpecs;
	private String providerName;
	private String providerCategory;
	private String usersChoice;
	
	public AdTemplateInfo() {
		details = new ArrayList<String>();
		technicalSpecs = new ArrayList<String>();
	}
	
	public void setAdTemplateId(long adTemplateId) {
		this.adTemplateId = adTemplateId;
	}
	public long getAdTemplateId() {
		return adTemplateId;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getHeadline() {
		return headline;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderCategory(String providerCategory) {
		this.providerCategory = providerCategory;
	}
	public String getProviderCategory() {
		return providerCategory;
	}

	public void setUsersChoice(String usersChoice) {
		this.usersChoice = usersChoice;
	}

	public String getUsersChoice() {
		return usersChoice;
	}
	public void setTechnicalSpecs(List<String> technicalSpecs) {
		this.technicalSpecs = technicalSpecs;
	}
	public List<String> getTechnicalSpecs() {
		return technicalSpecs;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

	public List<String> getDetails() {
		return details;
	}
	
}
