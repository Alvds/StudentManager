package com.alvydas.demo;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// dbutil obj paduoti datasource obj (connection pool)
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		}
		catch (Exception exc) {
			throw new ServletException(exc);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			// gauti command parametra
			String theCommand = request.getParameter("command");
			
			// jei command = null , ismesti lista
			if (theCommand == null) {
				theCommand = "LIST";
			}
			
			//switchinti pagal command argumenta
			switch (theCommand) {
			
			case "LIST":
				listStudents(request, response);
				break;
				
			case "ADD":
				addStudent(request, response);
				break;
				
			case "LOAD":
				loadStudent(request, response);
				break;
				
			case "UPDATE":
				updateStudent(request, response);
				break;
			
			case "DELETE":
				deleteStudent(request, response);
				break;
				
			default:
				listStudents(request, response);
			}
				
		}
		catch (Exception exc) {
			throw new ServletException(exc);
		}
		
	}

	private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		// getinti studenta pagal id
		String theStudentId = request.getParameter("studentId");
		
		// delete is db
		studentDbUtil.deleteStudent(theStudentId);
		
		// grazinti nauja lista
		listStudents(request, response);
	}

	private void updateStudent(HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		// gauti studento info
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		// sukurti studento obj
		Student theStudent = new Student(id, firstName, lastName, email);
		
		// updateinti
		studentDbUtil.updateStudent(theStudent);
		
		// grazinti studentu lista
		listStudents(request, response);
		
	}

	private void loadStudent(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {

		//gauti studento id
		String theStudentId = request.getParameter("studentId");
		
		// grazinti studenta is db pagal id
		Student theStudent = studentDbUtil.getStudent(theStudentId);
		
		// stetinti studenta i request attribute
		request.setAttribute("THE_STUDENT", theStudent);
		
		//uzkrauti jsp
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);		
	}

	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// gauti info is request
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");		
		
		// sukurti obj
		Student theStudent = new Student(firstName, lastName, email);
		
		// prideti i db
		studentDbUtil.addStudent(theStudent);
				
		// grazinti lista
		listStudents(request, response);
	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {

		
		List<Student> students = studentDbUtil.getStudents();
		
		
		request.setAttribute("STUDENT_LIST", students);
				
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}













