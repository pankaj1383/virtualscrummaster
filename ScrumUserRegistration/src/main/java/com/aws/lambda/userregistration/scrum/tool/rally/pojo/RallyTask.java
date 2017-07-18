package com.aws.lambda.userregistration.scrum.tool.rally.pojo;

import com.aws.lambda.userregistration.scrum.pojo.Task;
import com.aws.lambda.userregistration.scrum.pojo.User;
import com.aws.lambda.userregistration.scrum.pojo.UserStory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 
 * @author raja.pateriya
 *
 */
@JsonInclude(Include.NON_NULL)
public class RallyTask implements Task {
	private String name;
	private String id;
	private RallyUser owner;
	private String referenceID;
	private String actuals;
	private String doneHr;
	private String estimate;
	private String state;
	private String toDo;
	private RallyUserStory userStory;

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	@JsonProperty("Name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("FormattedID")
	public String getId() {
		return id;
	}

	@JsonProperty("FormattedID")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("Owner")
	public User getOwner() {
		return owner;
	}

	@JsonProperty("Owner")
	public void setOwner(RallyUser owner) {
		this.owner = owner;
	}

	@JsonProperty("_ref")
	public String getReferenceID() {
		return referenceID;
	}

	@JsonProperty("_ref")
	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}

	@JsonProperty("Actuals")
	public String getActuals() {
		return actuals;
	}

	@JsonProperty("Actuals")
	public void setActuals(String actuals) {
		this.actuals = actuals;
	}

	@JsonProperty("c_DoneHr")
	public String getDoneHr() {
		return doneHr;
	}

	@JsonProperty("c_DoneHr")
	public void setDoneHr(String doneHr) {
		this.doneHr = doneHr;
	}

	@JsonProperty("Estimate")
	public String getEstimate() {
		return estimate;
	}

	@JsonProperty("Estimate")
	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}

	@JsonProperty("State")
	public String getState() {
		return state;
	}

	@JsonProperty("State")
	public void setState(String state) {
		this.state = state;
	}

	@JsonProperty("ToDo")
	public String getToDo() {
		return toDo;
	}

	@JsonProperty("ToDo")
	public void setToDo(String toDo) {
		this.toDo = toDo;
	}

	@JsonProperty("WorkProduct")
	public UserStory getUserStory() {
		return userStory;
	}

	@JsonProperty("WorkProduct")
	@JsonDeserialize(as = RallyUserStory.class)
	public void setUserStory(UserStory userStory) {
		this.userStory = (RallyUserStory) userStory;
	}

}
