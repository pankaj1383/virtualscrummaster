package com.aws.lambda.taskupdaterhook.scrum.tool.rally.pojo;
/**
 * 
 * @author raja.pateriya
 *
 */
import com.aws.lambda.taskupdaterhook.scrum.pojo.UserStory;
import com.fasterxml.jackson.annotation.JsonProperty;
public class RallyUserStory implements UserStory{
	private String name;
	private String id;
	private String _ref;
	
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
	@JsonProperty("_ref")
	public String get_ref() {
		return _ref;
	}
	@JsonProperty("_ref")
	public void set_ref(String _ref) {
		this._ref = _ref;
	}
	
}
