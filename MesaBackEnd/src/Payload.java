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
	
	private int numUsers;
	private int numOnline;
	private int bufferSize;
	private String uptime;
	private String memUsage;
	private int numOverhead;
	private String netIP;
	
	public Payload() {
		dateModel = new DefaultListModel<String>();
		pilotModel = new DefaultListModel<String>();
		nameModel = new DefaultListModel<String>();
		flightModel = new DefaultListModel<String>();
		trainingModel = new DefaultListModel<String>();
		maintinenceModel = new DefaultListModel<String>();
		userModel = new DefaultListModel<String>();
		rankModel = new DefaultListModel<String>();
	}
	
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

	public int getNumUsers() {
		return numUsers;
	}

	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	public int getNumOnline() {
		return numOnline;
	}

	public void setNumOnline(int numOnline) {
		this.numOnline = numOnline;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(String memUsage) {
		this.memUsage = memUsage;
	}

	public int getNumOverhead() {
		return numOverhead;
	}

	public void setNumOverhead(int numOverhead) {
		this.numOverhead = numOverhead;
	}

	public String getNetIP() {
		return netIP;
	}

	public void setNetIP(String netIP) {
		this.netIP = netIP;
	}

	
}
