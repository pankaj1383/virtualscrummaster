package com.aws.lambda.taskupdaterhook.scrum.tool.versionone.pojo;
/**
 * 
 * @author raja.pateriya
 *
 */
public class V1UserStory {
	private String Name;
	private String FormattedID;
	private String _ref;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getFormattedID() {
		return FormattedID;
	}
	public void setFormattedID(String formattedID) {
		FormattedID = formattedID;
	}
	
}
