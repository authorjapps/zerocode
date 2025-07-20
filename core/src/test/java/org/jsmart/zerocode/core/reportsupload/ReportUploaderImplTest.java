package org.jsmart.zerocode.core.reportsupload;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

@RunWith(JukitoRunner.class)
public class ReportUploaderImplTest {
    private ReportUploaderImpl reportUploader;
    private Path tempDir;

    @Inject(optional = true)
    @Named("reports.repo")
    private String reportsRepo;

    @Inject(optional = true)
    @Named("reports.repo.username")
    private String reportsRepoUsername;

    @Inject(optional = true)
    @Named("reports.repo.token")
    private String reportsRepoToken;

    @Inject(optional = true)
    @Named("reports.repo.max.upload.limit.mb")
    private Integer reportsRepoMaxUploadLimitMb;

    public static class JukitoModule extends TestModule {
        @Override
        protected void configureTest() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("report_uploader.properties");
            install(applicationMainModule);
        }
    }

    @Before
    public void setUp() throws IOException {
        reportUploader = new ReportUploaderImpl();
        reportUploader.setDefaultUploadLimit();
        reportUploader.setReportsRepo(reportsRepo);
        reportUploader.setReportsRepoUsername(reportsRepoUsername);
        reportUploader.setReportsRepoToken(reportsRepoToken);
        reportUploader.setReportsRepoMaxUploadLimitMb(reportsRepoMaxUploadLimitMb);
        tempDir = Files.createTempDirectory("testRepo");
    }

    @Test
    public void testInitializeOrOpenGitRepository_cloneRepo() throws Exception {
        if(reportUploader.isAllRequiredVariablesSet()){
            File targetDir = new File(tempDir.toFile(),"clonerepo");
            Git git = reportUploader.initializeOrOpenGitRepository(targetDir.getAbsolutePath());
            assertTrue(new File(targetDir, ".git").exists());
            git.close();
        }
    }

    @Test
    public void testAddAndCommitChanges() throws Exception {
        if(reportUploader.isAllRequiredVariablesSet()){
            File targetDir = new File(tempDir.toFile(),"commitrepo");
            Git git = Git.init().setDirectory(targetDir).call();

            // Create a dummy file to commit
            File file = new File(targetDir, "dummy.txt");
            Files.write(file.toPath(), "data".getBytes());

            reportUploader.addAndCommitChanges(git);
            assertTrue(git.log().call().iterator().hasNext());
            git.close();
        }
    }

    @Test
    public void copyFile_fileExistsAndWithinSizeLimit_copiesFile() throws IOException {
        System.out.println(reportsRepo);
        File sourceFile = new File(tempDir.toFile(), "source.txt");
        Files.write(sourceFile.toPath(), "test content".getBytes());
        File targetDir = new File(tempDir.toFile(), "target");
        targetDir.mkdir();

        reportUploader.copyFile(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());

        File copiedFile = new File(targetDir, sourceFile.getName());
        assertTrue(copiedFile.exists());
    }

    @Test
    public void copyFile_fileExceedsSizeLimit_logsWarning() throws IOException {
        File sourceFile = new File(tempDir.toFile(), "largeFile.txt");
        Files.write(sourceFile.toPath(), new byte[3 * 1024 * 1024]); // 3 MB file
        File targetDir = new File(tempDir.toFile(), "target");
        targetDir.mkdir();
        reportUploader.copyFile(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());

        File copiedFile = new File(targetDir, sourceFile.getName());
        assertFalse(copiedFile.exists());
    }

    @After
    public void tearDown() throws IOException {
        if (tempDir != null) {
            Files.walk(tempDir)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

}
