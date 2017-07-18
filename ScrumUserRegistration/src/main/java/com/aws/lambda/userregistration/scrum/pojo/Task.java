package com.aws.lambda.userregistration.scrum.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author raja.pateriya
 *
 */
@JsonInclude(Include.NON_NULL)
public interface Task {

	public String getName();

	public void setName(String name);

	// public String getId();
	//
	// public void setId(String id);

	public String getReferenceID();

	public void setReferenceID(String referenceID);

	public String getActuals();

	public void setActuals(String actuals);

	public String getDoneHr();

	public void setDoneHr(String doneHr);

	public String getEstimate();

	public void setEstimate(String estimate);

	public String getState();

	public void setState(String state);

	public String getToDo();

	public void setToDo(String toDo);

public void setUserStory(UserStory usersStory);
public UserStory getUserStory();

}
