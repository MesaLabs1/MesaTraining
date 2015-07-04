import java.io.Serializable;

import javax.swing.DefaultListModel;

public class Payload implements Serializable {
	/*
	 * The generated UID MUST match on both the server and the client in order for the payload to sync.  
	 */
	private static final long serialVersionUID = -1476282277981561831L;
	
	private DefaultListModel<String> dateModel;
	private DefaultListModel<String> pilotModel;
	private DefaultListModel<String> nameModel;
	private DefaultListModel<String> flightModel;
	private DefaultListModel<String> trainingModel;
	private DefaultListModel<String> maintinenceModel;
	private DefaultListModel<String> userModel;
	private DefaultListModel<String> rankModel;
	
	public DefaultListModel<String> getDateModel() {
		return dateModel;
	}

	public void setDateModel(DefaultListModel<String> dateModel) {
		this.dateModel = dateModel;
	}

	public DefaultListModel<String> getPilotModel() {
		return pilotModel;
	}

	public void setPilotModel(DefaultListModel<String> pilotModel) {
		this.pilotModel = pilotModel;
	}

	public DefaultListModel<String> getNameModel() {
		return nameModel;
	}

	public void setNameModel(DefaultListModel<String> nameModel) {
		this.nameModel = nameModel;
	}

	public DefaultListModel<String> getFlightModel() {
		return flightModel;
	}

	public void setFlightModel(DefaultListModel<String> flightModel) {
		this.flightModel = flightModel;
	}

	public DefaultListModel<String> getTrainingModel() {
		return trainingModel;
	}

	public void setTrainingModel(DefaultListModel<String> trainingModel) {
		this.trainingModel = trainingModel;
	}

	public DefaultListModel<String> getMaintinenceModel() {
		return maintinenceModel;
	}

	public void setMaintinenceModel(DefaultListModel<String> maintinenceModel) {
		this.maintinenceModel = maintinenceModel;
	}

	public DefaultListModel<String> getUserModel() {
		return userModel;
	}

	public void setUserModel(DefaultListModel<String> userModel) {
		this.userModel = userModel;
	}

	public DefaultListModel<String> getRankModel() {
		return rankModel;
	}

	public void setRankModel(DefaultListModel<String> rankModel) {
		this.rankModel = rankModel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
