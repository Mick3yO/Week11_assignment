package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

// This class serves as the main driver for managing projects. Users can add, list, select, update and delete projects through the operations available.
public class ProjectsApp {

	// Scanner object used for capturing user input from the console.
	private Scanner scanner = new Scanner(System.in);
	
	// Service class for managing projects.
	private ProjectService projectService = new ProjectService();
	
	// Currently selected project, null if no project is selected.
	private Project curProject;
		
	// List of available operations that a user can perform.
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List projects",
		"3) Select a project",
		"4) Update project details",
		"5) Delete a project"
	);

	// Entry point of the program.
	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}
	
	// Method that continues to process user selections until the user decides to exit.
	private void processUserSelections() {
		boolean done = false;

		while(!done) {
			try {
				int selection = getUserSelection();
				switch(selection) {
					case -1:
						// Exit the menu.
						done = exitMenu();
						break;
					case 1:
						// Create a new project.
						createProject();
						break;	
					case 2:
						// List all projects.
						listProjects();
						break;
					case 3:
						// Select a project for further operations.
						selectProject();
						break;
					case 4:
						// Update the details of the currently selected project.
						updateProjectDetails();
						break;
					case 5:
						// Delete a project.
						deleteProject();
						break;
					default:
						System.out.println("\n" + selection + " is not valid. Try again.");
				}
			} catch (Exception e) {
				System.out.println("\nError" + e.toString() + " Try again.");
				e.printStackTrace();
			}
		}	
	}

	// Method to delete a project. 
	private void deleteProject() {
		// List all projects before deletion.
		listProjects();

		Integer projectId = getIntInput("Enter the ID of the project to delete");

		// Use the service to delete the project.
		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was successfully deleted.");

		// If the deleted project was the currently selected project, unselect it.
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}	
	}

	// Method to update the details of the currently selected project.
	private void updateProjectDetails() {
		// If no project is selected, prompt the user to select a project.
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}

		// Get updated details from the user.
		String projectName = getStringInput("Enter the project name (" + curProject.getProjectName() + ")");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours (" + curProject.getEstimatedHours() + ")");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours (" + curProject.getActualHours() + ")");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) (" + curProject.getDifficulty() + ")");
		String notes = getStringInput("Enter the project notes (" + curProject.getNotes() + ")");
		
		// Create a new project object with updated details and send it to the service to update in the database.
		Project project = new Project();
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		projectService.modifyProjectDetails(project);
		
		// Update the current project object to reflect the changes.
		curProject = projectService.fetchProjectbyId(curProject.getProjectId());	
	}

	// Method to list all projects.
	private List<Project> listProjects() {
		List <Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out
				.println("   " + project.getProjectId()  
				+ ": " + project.getProjectName()));
		
		return projects;
	}

	// Method to select a project for further operations.
	private void selectProject() {
		// List all projects before selection.
		listProjects();
		
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		// Fetch the project object using the service.
		curProject = projectService.fetchProjectbyId(projectId);	
	}

	// Method to create a new project.
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		// Use the service to add the new project to the database.
		Project dbProject = projectService.addProject(project);
		System.out.println("You added this project:\n" + dbProject);
	}

	// Method to get user selection for operation.
	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("\nEnter a menu selection");
		
		return Objects.isNull(input) ? -1 : input;
	}
		
	// Method to print available operations to the user.
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press Enter to quit:");
		
		operations.forEach(line -> System.out.println("   " + line));
		
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

	// Method to get integer input from the user.
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	// Method to get string input from the user.
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();
		
		return line.isBlank() ? null : line.trim();
	}
	
	// Method to exit the menu.
	private boolean exitMenu() {
		System.out.println("Exiting the menu...");
		return true;
	}
	
	// Method to get decimal input from the user.
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
}



