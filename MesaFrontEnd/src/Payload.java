import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

public class Payload implements Serializable {
	/*
	 * The generated UID MUST match on both the server and the client in order for the payload to sync.  
	 */
	private static final long serialVersionUID = -1476282277981561831L;

	private DefaultListModel<String> dateModel;
	private DefaultListModel<String> pilotModel;
	private DefaultListModel<String> nameModel;

	private String[] flightLogs;
	private String[] maintinenceLogs;
	private String[] trainingLogs;

	private DefaultListModel<String> userModel;
	private DefaultListModel<String> rankModel;

	private ArrayList<Entry> entries;

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
		userModel = new DefaultListModel<String>();
		rankModel = new DefaultListModel<String>();

		entries = new ArrayList<Entry>();
	}

	public DefaultListModel<String> getDateModel() {
		return dateModel;
	}

	public void setDateModel(DefaultListModel<String> dateModel) {
		DefaultListModel<String> actualDates = new DefaultListModel<String>();
		for (int i = 0; i < dateModel.getSize(); i++) {
			String date = "";
			String value = dateModel.getElementAt(i);
			
			String month = value.substring(2, 4);
			String day = value.substring(0, 2);
			String year = value.substring(4, 8);
			
			String hour = value.substring(9, 11);
			String min = value.substring(11, 13);
			String sec = value.substring(13, 15);
			
			date = month + "/" + day + "/" + year + " at " + hour + ":" + min + ":" + sec;
			if (Integer.valueOf(hour) >= 12) {
				date += " PM";
			}else {
				date += " AM";
			}
			actualDates.addElement(date);
		}
		this.dateModel = actualDates;
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

	/**
	 * Call this method when you're done uploading to the class, so you can parse the changes.
	 */
	public void index() {
		for (int i = 0; i < dateModel.getSize(); i++) {
			Entry entry = new Entry(pilotModel.getElementAt(i), nameModel.getElementAt(i), dateModel.getElementAt(i));
			entry.setFlightData(flightLogs[i].split("~"));
			entry.setMaintinenceData(maintinenceLogs[i].split("~"));
			entry.setTrainingData(trainingLogs[i].split("~"));

			entries.add(entry);
		}
	}

	public ArrayList<Entry> getDataByPilot(String p) {
		ArrayList<Entry> output = new ArrayList<Entry>();
		for (Entry e : entries) {
			if (e.getPilot().equals(p)) {
				output.add(e);
			}
		}
		return output;
	}

	public ArrayList<Entry> getDataByDate(String d) {
		ArrayList<Entry> output = new ArrayList<Entry>();
		for (Entry e : entries) {
			if (e.getDate().equals(d)) {
				output.add(e);
			}
		}
		return output;
	}

	public ArrayList<Entry> getDataByAircraft(String a) {
		ArrayList<Entry> output = new ArrayList<Entry>();
		for (Entry e : entries) {
			if (e.getAircraft().equals(a)) {
				output.add(e);
			}
		}
		return output;
	}

	public String[] getFlightLogs() {
		return flightLogs;
	}

	public void setFlightLogs(String[] flightLogs) {
		this.flightLogs = flightLogs;
	}

	public String[] getMaintinenceLogs() {
		return maintinenceLogs;
	}

	public void setMaintinenceLogs(String[] maintinenceLogs) {
		this.maintinenceLogs = maintinenceLogs;
	}

	public String[] getTrainingLogs() {
		return trainingLogs;
	}

	public void setTrainingLogs(String[] trainingLogs) {
		this.trainingLogs = trainingLogs;
	}

	public class Entry {
		private String pilot;
		private String aircraft;
		private String date;

		private String[] flightData;
		private String[] maintinenceData;
		private String[] trainingData;

		public String getPilot() {
			return pilot;
		}

		public String getAircraft() {
			return aircraft;
		}

		public String getDate() {
			return date;
		}

		public String[] getFlightData() {
			return flightData;
		}

		public void setFlightData(String[] flightData) {
			this.flightData = flightData;
		}

		public String[] getMaintinenceData() {
			return maintinenceData;
		}

		public void setMaintinenceData(String[] maintinenceData) {
			this.maintinenceData = maintinenceData;
		}

		public String[] getTrainingData() {
			return trainingData;
		}

		public void setTrainingData(String[] trainingData) {
			this.trainingData = trainingData;
		}

		public Entry(String p, String a, String d) {
			pilot = p;
			aircraft = a;
			date = d;
		}

	}


}
