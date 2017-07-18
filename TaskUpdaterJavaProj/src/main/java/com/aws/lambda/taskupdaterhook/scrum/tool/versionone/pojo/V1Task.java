package com.aws.lambda.taskupdaterhook.scrum.tool.versionone.pojo;
/**
 * 
 * @author raja.pateriya
 *
 */
public class V1Task {
private String Name;
private String FormattedID;
private V1User Owner;
private String _ref;
private String Actuals;
private String c_DoneHr;
private String Estimate;
private String State;
private String ToDo;

public String getActuals() {
	return Actuals;
}
public void setActuals(String actuals) {
	Actuals = actuals;
}
public String getC_DoneHr() {
	return c_DoneHr;
}
public void setC_DoneHr(String c_DoneHr) {
	this.c_DoneHr = c_DoneHr;
}
public String getEstimate() {
	return Estimate;
}
public void setEstimate(String estimate) {
	Estimate = estimate;
}
public String getState() {
	return State;
}
public void setState(String state) {
	State = state;
}
public String getToDo() {
	return ToDo;
}
public void setToDo(String toDo) {
	ToDo = toDo;
}
public String get_ref() {
	return _ref;
}
public void set_ref(String _ref) {
	this._ref = _ref;
}
private V1UserStory WorkProduct;

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
public V1User getOwner() {
	return Owner;
}
public void setOwner(V1User owner) {
	Owner = owner;
}
public V1UserStory getWorkProduct() {
	return WorkProduct;
}
public void setWorkProduct(V1UserStory workProduct) {
	WorkProduct = workProduct;
}
}
