// This is the service class for Project entity. 
// It uses methods from ProjectDao to interact with the database.
package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
    // Creating an instance of the ProjectDao class
	private ProjectDao projectDao = new ProjectDao();

    // Method to add a new project. It calls the insertProject method from the ProjectDao class
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

    // Method to fetch all projects. 
    // It calls the fetchAllProjects method from the ProjectDao class 
    // and sorts them by project ID before returning
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects()
				.stream()
				.sorted((r1, r2) -> r1.getProjectId() - r2.getProjectId())
				.collect(Collectors.toList());
	}

    // Method to fetch a project by its ID.
    // It calls the fetchProjectById method from the ProjectDao class. 
    // If no project with that ID is found, it throws an exception.
	public Project fetchProjectbyId(Integer projectId) {
		return projectDao.fetchProjectById(projectId)
				.orElseThrow(() -> new NoSuchElementException(
				"Project with ID ="+ projectId + " does not exist."));
	}

    // Method to modify the details of a project.
    // It calls the modifyProjectDetails method from the ProjectDao class. 
    // If no project with the provided ID is found, it throws an exception.
	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		}
		
	}

    // Method to delete a project by its ID.
    // It calls the deleteProject method from the ProjectDao class. 
    // If no project with the provided ID is found, it throws an exception.
	public void deleteProject(Integer projectId) {
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
	}
}

