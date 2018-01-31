package restmule.evaluation;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import restmule.evaluation.GithubRepoSearchRunner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore("to be executed manually")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GithubRepoSearchRunnerTest {
	
	static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hhmmss");
        System.setProperty("current.date", dateFormat.format(new Date()));
    }
	
	private static final String GITHUB_CREDENTIALS_FILE_LOCATION = "github-credentials.txt";
	private String githubUser = "";
	private String githubPass = "";
	
	private static String SEARCH_REPORT_PATH = "data/";
	private static String ECSSAL_MODEL_FILE_LOCATION = SEARCH_REPORT_PATH + "analysis/ecssal-analysis-model.xmi";
	
	private String readGithubUserFromFile() {
		try {
			String line = Files.readAllLines(Paths.get(GITHUB_CREDENTIALS_FILE_LOCATION)).get(0);
			githubUser = line.substring(0, line.indexOf(","));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return githubUser;
	}
	
	private String readGithubUserFromFile(int line) {
		try {
			String lineContent = Files.readAllLines(Paths.get(GITHUB_CREDENTIALS_FILE_LOCATION)).get(line);
			githubUser = lineContent.substring(0, lineContent.indexOf(","));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return githubUser;
	}
	
	private String readGithubPassFromFile(int line) {
		try {
			String lineContent = Files.readAllLines(Paths.get(GITHUB_CREDENTIALS_FILE_LOCATION)).get(line);
			githubPass = lineContent.substring(lineContent.indexOf(",")+1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return githubPass;
	}	
	
	private String readGithubPassFromFile() {
		try {
			String line = Files.readAllLines(Paths.get(GITHUB_CREDENTIALS_FILE_LOCATION)).get(0);
			githubPass = line.substring(line.indexOf(",")+1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return githubPass;
	}	

//	@Ignore("to be executed manually")
	@Test	
	public void testGMF() {
		String githubUser = readGithubUserFromFile(1);
		String githubPass = readGithubPassFromFile(1);
		
		GithubRepoSearchRunner ghSearchRunner = new GithubRepoSearchRunner("data-gmf/", githubUser, githubPass, "figure extension:gmfgraph");
		ghSearchRunner.runSearch();
	}
	
//	@Ignore("to be executed manually")
	@Test	
	public void testSirius() {
		String githubUser = readGithubUserFromFile(2);
		String githubPass = readGithubPassFromFile(2);
		
		GithubRepoSearchRunner ghSearchRunner = new GithubRepoSearchRunner("data-sirius/", githubUser, githubPass, "node extension:odesign");
		ghSearchRunner.runSearch();
	}
	
//	@Ignore("to be executed manually")
	@Test	
	public void testEugenia() {
		String githubUser = readGithubUserFromFile(0);
		String githubPass = readGithubPassFromFile(0);
		
		GithubRepoSearchRunner ghSearchRunner = new GithubRepoSearchRunner("data-eugenia/", githubUser, githubPass, "\"gmf.diagram\" extension:ecore");
		ghSearchRunner.runSearch();
	}
	
//	@Test
//    public void listFiles() throws Exception {
//        GHRepository repo = gitHub.getRepository("stapler/stapler");
//        PagedIterable<GHCommit> commits = repo.queryCommits().path("pom.xml").list(); // do this for every file based on technology-extension
//        for (GHCommit commit : Iterables.limit(commits, 10)) {
//            GHCommit expected = repo.getCommit( commit.getSHA1() );
//            assertEquals(expected.getFiles().size(), commit.getFiles().size());
//        }
//    }

}
