package org.jsmart.zerocode.core.reportsupload;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ReportUploaderImplTest {


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

    private ReportUploaderImpl reportUploader;

    private Path tempDir;

    @Before
    public void setUp() throws IOException {
        reportUploader = new ReportUploaderImpl();
        reportUploader.setDefaultUploadLimit();
        tempDir = Files.createTempDirectory("testRepo");
    }

    @After
    public void tearDown() throws IOException {
        if (tempDir != null) {
            Files.walk(tempDir)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

 /*
    @Test
    public void initializeOrOpenGitRepository_existingRepository_pullsLatestChanges() throws IOException, GitAPIException {
        File gitDir = new File(tempDir.toFile(), ".git");
        Git.init().setDirectory(tempDir.toFile()).call();

        Git git = reportUploader.initializeOrOpenGitRepository(tempDir.toString());

        assertNotNull(git);
        assertTrue(gitDir.exists());
    }

  */

    @Test
    public void copyFile_fileExistsAndWithinSizeLimit_copiesFile() throws IOException {
        System.out.println(reportsRepoUsername);

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

}
