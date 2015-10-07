import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.xml.sax.SAXException;

/**
 * CurriculumDatabase takes the XML database for the lesson/quiz system, and prepares it to be serialized over the network.
 * 
 * Remember, we never want to call on data the moment we need it, because that creates delays. ADO is all about doing things 
 * in advance of or at the same time as when we need it.
 * @author hackjunky
 *
 */
public class CurriculumDatabase {
	//Populate Java classes for XML transposition.
	DocumentBuilderFactory dbFactory;
	File dbFile;
	DocumentBuilder dbBuilder;
	Document doc;

	//Mandatory helper classes, and payload pointer.
	Utils util;
	Payload payload;
	UI ui;

	/**
	 * Constructor for Curriculum Database Manager, taking in a Utility pointer, a UI pointer, and a Payload pointer. These should
	 * already be instantiated by another class.
	 * @param u Utility pointer.
	 * @param ui UI pointer.
	 * @param p Payload pointer.
	 */
	public CurriculumDatabase(Utils u, UI ui, Payload p) {
		util = u;
		this.ui = ui;
		payload = p;
		//Create the XML file if it does not exist.
		if (VerifyFS()) {
			Refresh();
		}else {
			MakeFS();
		}
	}

	/**
	 * If the XML file we want exists, return true.
	 * @return True if Curriculum.XML exists, false if not.
	 */
	public boolean VerifyFS() {
		//Does the XML file exist? 
		if (new File("curriculum.xml").exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Let's create a file system since we're missing one. In this case, the XML template is required, so we always
	 * ship with one.
	 */
	public void MakeFS() {
		util.Log("Making the curriculum template...");
		File dbtemp = new File("curriculum_template.xml");
		if (!new File("curriculum.xml").exists()) {
			try {
				Files.copy(new File("curriculum_template.xml").toPath(), new File("curriculum.xml").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Failed to create proper FS. Unexpected behavior could occur.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * We save and reload the document builder, since we want to have it reflect our current changes in the buffer.
	 */
	public void Refresh() {		
		dbFile = new File("curriculum.xml");
		dbFactory = DocumentBuilderFactory.newInstance();
		dbBuilder = null;
		try {
			dbBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		doc = null;
		try {
			doc = dbBuilder.parse(dbFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();

		GeneratePayload();
	}

	/**
	 * We take what changes we've made and write them using DOM to an XML file. Then, we refresh.
	 */
	public void Save() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(dbFile);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		Refresh();
	}

	/**
	 * SYNCHRONOUS CertifyUser allows for the frontend to call for the certification of a user in completing or passing a quiz,
	 * or reading material. 
	 * @param target The name of the lesson/quiz to certify for.
	 * @param user The name of the user to certify for.
	 */
	synchronized public void CertifyUser(String target, String user) {
		NodeList targetList = doc.getElementsByTagName(target);
		for (int i = 0; i < targetList.getLength(); i++) {
			Node nNode = targetList.item(i);
			Element eTarget = (Element)nNode;
			String users = eTarget.getAttribute("Users");
			if (users == null) {
				//Does not support user-marking!
				util.Log("Cannot certify " + user + " in curriculum entity " + target +". Not certifiable!");
			}else {
				users += user + ";";
				eTarget.setAttribute("Users", users);
				util.Log("Certifying " + user + " for completing + " + target);
			}
		}
	}

	/**
	 * Here we generate the payload. What this means is we take the existing Payload class, and read the XML file here,
	 * then upload the changes to Payload, awaiting the next synchronization. 
	 */
	synchronized public void GeneratePayload() {
		util.Log("Reticulating curriculum structure...");
		//Search for the Lessons master.
		NodeList searchList = doc.getElementsByTagName("Lessons");
		for (int i = 0; i < searchList.getLength(); i++) {
			Node nNode = searchList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				for (int a = 0; a < nNode.getChildNodes().getLength(); a++) {
					Node subNode = nNode.getChildNodes().item(a);
					if (subNode.getNodeType() == Node.ELEMENT_NODE) {
						Element subElement = (Element)subNode;
						//Is it a Lesson?
						if (subElement.getNodeName().toLowerCase().startsWith("lesson")) {
							Payload.Curriculum.Lesson lesson = payload.getCurriculum().CreateLesson();
							//Get the Title and the Users who have passed for this lesson
							String lessonTitle = ((Element)subNode).getAttribute("Title");
							String lessonUsers = ((Element)subNode).getAttribute("Users");
							int pageCount = 0; 			//Reference for logging
							for (int b = 0; b < subNode.getChildNodes().getLength(); b++) {
								Node subSubNode = subNode.getChildNodes().item(b);
								if (subSubNode.getNodeType() == Node.ELEMENT_NODE) {
									Element subSubElement = (Element)subSubNode;
									//Get a Data sheet for this lesson.
									if (subSubElement.getNodeName().toLowerCase().startsWith("data")) {
										String sectionTitle = subSubElement.getAttribute("Title");
										//Get the title of this section.
										ArrayList<String> pages = new ArrayList<String>();
										for (int c = 0; c < subNode.getChildNodes().item(b).getChildNodes().getLength(); c++) {
											Node dataNode = subNode.getChildNodes().item(b).getChildNodes().item(c);
											if (dataNode.getNodeType() == Node.ELEMENT_NODE) {
												//Parse and load all "content" in this data sheet.
												Element data = (Element)dataNode;
												String page = data.getAttribute("Content");
												pages.add(page);
												pageCount++;
											}
										}
										//Add the section to the Lesson plan.
										lesson.AddSection(pages, sectionTitle);
									}
								}
							}	
							//Set the title, and the users.
							lesson.setTitle(lessonTitle);
							if (lessonUsers.length() > 0) {
								lessonUsers = lessonUsers.substring(0, lessonUsers.length() - 1);
								String[] splitUsers = lessonUsers.split(";");
								for (int d = 0; d < splitUsers.length; d++) {
									lesson.AddUser(splitUsers[d]);
								}
							}
							payload.getCurriculum().AddLesson(lesson);
							//Add the Lesson to the Curriculum.
							util.Log("LESSON: " + lessonTitle);
							util.Log("\t " + lesson.getSections().size() + " sections");
							util.Log("\t " + pageCount + " pages");
							util.Log("\t " + lesson.getUsersCompleted().size() + " users passed");
						}else if (subElement.getNodeName().toLowerCase().startsWith("quiz")) {
							//Is it a Quiz?
							Payload.Curriculum.Quiz quiz = payload.getCurriculum().CreateQuiz();
							String quizTitle = ((Element)subNode).getAttribute("Title");
							String quizUsers = ((Element)subNode).getAttribute("Users");
							//Get the Title of the Quiz, and the Users who have passed it.
							//Set them.
							quiz.setTitle(quizTitle);
							if (quizUsers.length() > 0) {
								quizUsers = quizUsers.substring(0, quizUsers.length() - 1);
								String[] splitUsers = quizUsers.split(";");
								for (int d = 0; d < splitUsers.length; d++) {
									quiz.AddUser(splitUsers[d]);
								}
							}
							//Read through the list, accessing each question, and appending the data to Payload.
							for (int b = 0; b < subNode.getChildNodes().getLength(); b++) {
								Element content = (Element)subNode.getChildNodes().item(b);
								String question = content.getAttribute("Question");
								String optionsStr = content.getAttribute("Options");
								String[] options = optionsStr.split(";");
								String answerStr = content.getAttribute("Answer");
								int answer = Integer.parseInt(answerStr);
								Payload.Curriculum.Quiz.QuizQuestion quizQuestion = quiz.CreateQuestion();
								quizQuestion.SetQuestion(question);
								quizQuestion.SetAnswers(options);
								quizQuestion.SetResponse(answer);
								quiz.AddQuestion(quizQuestion);
							}
							util.Log("QUIZ: " + quizTitle);
							util.Log("\t " + quiz.questions.size() + " questions");
							util.Log("\t " + quiz.getUsersCompleted().size() + " users passed");
							//Add it to the Curriculum.
							payload.getCurriculum().AddQuiz(quiz);
						}else {
							//If its not a lesson or a quiz, we should let the user know.
							util.Log("Unrecognized curriculum type! Cannot generate support for " + subNode.getNodeValue() + "!");
						}
					}
				}
			}
		}
		util.Log("Curriculum reticulation complete, payload is live.");
		ui.accessCount++;
	}


}
