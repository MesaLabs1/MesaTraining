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
	private String[] RepairLogs;
	private String[] trainingLogs;

	private DefaultListModel<String> userModel;
	private DefaultListModel<String> rankModel;

	private ArrayList<Entry> entries;

	private Curriculum curriculum;

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
		
		curriculum = new Curriculum();
	}

	public DefaultListModel<String> getDateModel() {
		return dateModel;
	}

	public void setDateModel(DefaultListModel<String> dateModel) {
		DefaultListModel<String> actualDates = new DefaultListModel<String>();
		for (int i = 0; i < dateModel.getSize(); i++) {
			String date = "";
			String value = dateModel.getElementAt(i);

			if (value.length() > 0) {
				String month = value.substring(2, 4);
				String day = value.substring(0, 2);
				String year = value.substring(4, 8);

				//				String hour = value.substring(9, 11);
				//				String min = value.substring(11, 13);
				//				String sec = value.substring(13, 15);

				date = month + "/" + day + "/" + year;
				//				if (Integer.valueOf(hour) >= 12) {
				//					date += " PM";
				//				}else {
				//					date += " AM";
				//				}
				actualDates.addElement(date);
			}
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

	public void ClearEntries() {
		entries = new ArrayList<Entry>();
	}

	public void AddEntry(Entry e) {
		entries.add(e);
	}
	
	public Curriculum getCurriculum() {
		return curriculum;
	}

	public Entry CreateBlankEntry(String p, String a, String d) {
		return new Entry(p, a, d);
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

	public String[] getRepairLogs() {
		return RepairLogs;
	}

	public void setRepairLogs(String[] RepairLogs) {
		this.RepairLogs = RepairLogs;
	}

	public String[] getTrainingLogs() {
		return trainingLogs;
	}

	public void setTrainingLogs(String[] trainingLogs) {
		this.trainingLogs = trainingLogs;
	}

	public class Entry implements Serializable{
		private static final long serialVersionUID = -2799008524139349499L;

		private String pilot;
		private String aircraft;
		private String date;

		private String flightData;
		private String RepairData;
		private String trainingData;

		public String getPilot() {
			return pilot;
		}

		public String getAircraft() {
			return aircraft;
		}

		public String getDate() {
			String month = date.substring(2, 4);
			String day = date.substring(0, 2);
			String year = date.substring(4, 8);

			return month + "/" + day + "/" + year;
		}

		public String getRawDate() {
			return date;
		}

		public String getFlightData() {
			if (flightData == null) {
				return "";
			}
			return flightData;
		}

		public void setFlightData(String flightData) {
			this.flightData = flightData;
		}

		public String getRepairData() {
			if (RepairData == null) {
				return "";
			}
			return RepairData;
		}

		public void setRepairData(String RepairData) {
			this.RepairData = RepairData;
		}

		public String getTrainingData() {
			if (trainingData == null) {
				return "";
			}
			return trainingData;
		}

		public void setTrainingData(String trainingData) {
			this.trainingData = trainingData;
		}

		public Entry(String p, String a, String d) {
			pilot = p;
			aircraft = a;
			date = d;
		}

	}

	public class Curriculum implements Serializable {
		private static final long serialVersionUID = 6647307836727889362L;
		ArrayList<CBase> lessonPlan;
		
		public Curriculum() {
			lessonPlan = new ArrayList<CBase>();
		}
		
		public Lesson CreateLesson() {
			return new Lesson();
		}
		
		public Quiz CreateQuiz() {
			return new Quiz();
		}
		
		public void AddLesson(Lesson l) {
			lessonPlan.add(l);
		}
		
		public void AddQuiz(Quiz q) {
			lessonPlan.add(q);
		}
		
		public abstract class CBase implements Serializable {
			private static final long serialVersionUID = -375548600471048655L;
			
			private ArrayList<String> usersCompleted;
			
			public ArrayList<String> getUsersCompleted() {
				return usersCompleted;
			}

			private String title;
			
			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public CBase() {
				usersCompleted = new ArrayList<String>();
			}
			
			public boolean HasCompleted(String user) {
				for (String s : usersCompleted) {
					if (user.equals(s)) {
						return true;
					}
				}
				return false;
			}
			
			public void AddUser(String user) {
				usersCompleted.add(user);
			}
		}
		
		public class Lesson extends CBase implements Serializable {
			private static final long serialVersionUID = 5872060605131951672L;
			ArrayList<Section> sections;
		
			public Lesson() {
				sections = new ArrayList<Section>();
			}
			
			public void AddSection(ArrayList<String> pages, String title) {
				Section s = new Section(pages);
				s.setTitle(title);
				sections.add(s);
			}
			
			public ArrayList<Section> getSections() {
				return sections;
			}

			
			public class Section implements Serializable {
				private static final long serialVersionUID = -1776884185653143123L;
				ArrayList<String> pages;
				String title;
				
				public String getTitle() {
					return title;
				}

				public void setTitle(String title) {
					this.title = title;
				}

				public Section(ArrayList<String> p) {
					pages = p;
				}
				
				public void addPage(String p) {
					pages.add(p);
				}
				
				public void addPages(String[] p) {
					for (int i = 0; i < p.length; i++) {
						pages.add(p[i]);
					}
				}
				
				public void removePage(int a) {
					pages.remove(a);
				}
			}
		}
		
		public class Quiz extends CBase implements Serializable {
			private static final long serialVersionUID = -5334137890876651888L;
			ArrayList<QuizQuestion> questions;
			
			public Quiz() {
				questions = new ArrayList<QuizQuestion>();
			}
			
			public QuizQuestion GetQuestion(int i) {
				return questions.get(i);
			}
			
			public void AddQuestion(QuizQuestion q) {
				questions.add(q);
			}
			
			public void AddQuestion(QuizQuestion q, int i) {
				questions.add(i, q);
			}
			
			public QuizQuestion CreateQuestion() {
				return new QuizQuestion();
			}
			
			public class QuizQuestion implements Serializable {
				private static final long serialVersionUID = -1131913365991982401L;
				
				String question;
				int answer;
				String[] responses;
				
				public QuizQuestion() {
					responses = new String[5];
				}
				
				public void SetQuestion(String s) {
					question = s;
				}
				
				public void SetResponse(int resp) {
					answer = resp;
				}
				
				public void SetAnswers(String[] r) {
					for (int i = 0; i < 5; i++) {
						 responses[i] = r[i];
					}
				}
				
				public void SetAnswer(int i, String c) {
					if (i < responses.length) {
						responses[i] = c;
					}
				}
			}
		}
	}
}
