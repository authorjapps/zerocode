package org.jsmart.zerocode.core.reportsupload;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jsmart.zerocode.core.constants.ZeroCodeReportConstants;
import org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

public class ReportUploaderImpl implements ReportUploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeReportGeneratorImpl.class);

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


    public void uploadReport() {
        if (!isAllRequiredVariablesSet()) {
            LOGGER.warn("One or more required variables are not set. Skipping report upload.");
            return;
        }

        setDefaultUploadLimit();
        createParentDirectoryIfNotExists(new File(ZeroCodeReportConstants.REPORT_UPLOAD_DIR));

        try (Git git = initializeOrOpenGitRepository(ZeroCodeReportConstants.REPORT_UPLOAD_DIR)) {
            addRemoteRepositoryIfMissing(git);

            //Copy files to the repository
            copyFile(ZeroCodeReportConstants.TARGET_FILE_NAME, ZeroCodeReportConstants.REPORT_UPLOAD_DIR);
            copyFile(
                    ZeroCodeReportConstants.TARGET_FULL_REPORT_DIR + ZeroCodeReportConstants.TARGET_FULL_REPORT_CSV_FILE_NAME,
                    ZeroCodeReportConstants.REPORT_UPLOAD_DIR);

            /*
            copyFirstFileUnder2MBMatchingPattern(
                    ZeroCodeReportConstants.SUREFIRE_REPORT_DIR,
                    "*.xml",
                    ZeroCodeReportConstants.REPORT_UPLOAD_DIR
            );
            */
            copyFirstFileUnder2MBMatchingPattern(
                    ZeroCodeReportConstants.TARGET_FULL_REPORT_DIR + "/logs",
                    "*.log",
                    ZeroCodeReportConstants.REPORT_UPLOAD_DIR
            );
            addAndCommitChanges(git);
            pushToRemoteRepository(git);
        } catch (Exception e) {
            LOGGER.warn("Report upload failed: {}", e.getMessage());
        }
    }

    protected void addRemoteRepositoryIfMissing(Git git) throws URISyntaxException, GitAPIException {
        if (git.remoteList().call().isEmpty()) {
            LOGGER.debug("Adding remote repository: {}", reportsRepo);
            git.remoteAdd().setName("origin").setUri(new URIish(reportsRepo)).call();
        } else {
            LOGGER.debug("Remote repository already exists.");
        }
    }

    protected void addAndCommitChanges(Git git) throws GitAPIException {
        git.add().addFilepattern(".").call();
        LOGGER.debug("Added all files to the Git index.");

        git.commit()
                .setMessage("Updated files")
                .setAuthor(reportsRepoUsername, reportsRepoUsername)
                .call();
        LOGGER.debug("Committed changes.");
    }

    protected void pushToRemoteRepository(Git git) throws GitAPIException {
        git.push()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(reportsRepoUsername, reportsRepoToken))
                .call();
        LOGGER.debug("Pushed changes to remote repository!");
    }

    protected boolean isAllRequiredVariablesSet() {
        return reportsRepo != null && !reportsRepo.isEmpty() &&
                reportsRepoUsername != null && !reportsRepoUsername.isEmpty() &&
                reportsRepoToken != null && !reportsRepoToken.isEmpty();
    }

    protected void setDefaultUploadLimit() {
        if (reportsRepoMaxUploadLimitMb == null) {
            reportsRepoMaxUploadLimitMb = 2;
            LOGGER.debug("reportsRepoMaxUploadLimitMb is not set. Defaulting to 2 MB.");
        }
    }

    protected void createParentDirectoryIfNotExists(File repoDir) {
        File parentDir = repoDir.getParentFile();
        if (!parentDir.exists() && parentDir.mkdirs()) {
            LOGGER.debug("Directory created: {}", parentDir.getAbsolutePath());
        } else if (!parentDir.exists()) {
            LOGGER.warn("Failed to create directory: {}", parentDir.getAbsolutePath());
        }
    }

    protected Git initializeOrOpenGitRepository(String repoUploadDir) throws IOException, GitAPIException {
        if (new File(repoUploadDir, ".git").exists()) {
            LOGGER.debug("Existing Git repository found.");
            Git git = Git.open(new File(repoUploadDir));
            LOGGER.debug("Pulling latest changes from remote repository...");
            git.pull()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(reportsRepoUsername, reportsRepoToken))
                    .call();
            return git;
        } else {
            LOGGER.debug("Initializing a new Git repository...");
            return Git.cloneRepository()
                    .setURI(reportsRepo)
                    .setDirectory(new File(repoUploadDir))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(reportsRepoUsername, reportsRepoToken))
                    .call();
        }
    }

    protected void copyFile(String sourcePath, String targetDirPath) throws IOException {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            LOGGER.warn("File not found: {}", sourcePath);
            return;
        }
        if (sourceFile.length() <= reportsRepoMaxUploadLimitMb * 1024 * 1024) {
            Files.copy(sourceFile.toPath(), Paths.get(targetDirPath, sourceFile.getName()), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.debug("File copied: {}", sourceFile.getName());
        } else {
            LOGGER.warn("File size exceeds {} MB. Skipping copy: {}", reportsRepoMaxUploadLimitMb, sourceFile.getName());
        }
    }

    protected void copyFirstFileUnder2MBMatchingPattern(String dirPath, String globPattern, String targetDirPath) throws IOException {
        Path dir = Paths.get(dirPath);
        final long maxSizeInBytes = reportsRepoMaxUploadLimitMb * 1024 * 1024;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, globPattern)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry) && Files.size(entry) <= maxSizeInBytes) {
                    Path target = Paths.get(targetDirPath, entry.getFileName().toString());
                    Files.copy(entry, target, StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.info("Copied first matched file: {}", entry.getFileName());
                    return;
                }
            }
            LOGGER.warn("No file under 2MB found matching pattern: {}", globPattern);
        }
    }


    public void setReportsRepo(String reportsRepo) {
        this.reportsRepo = reportsRepo;
    }

    public void setReportsRepoUsername(String reportsRepoUsername) {
        this.reportsRepoUsername = reportsRepoUsername;
    }

    public void setReportsRepoToken(String reportsRepoToken) {
        this.reportsRepoToken = reportsRepoToken;
    }

    public void setReportsRepoMaxUploadLimitMb(Integer reportsRepoMaxUploadLimitMb) {
        if (reportsRepoMaxUploadLimitMb == null) {
            this.reportsRepoMaxUploadLimitMb = 2;
        }else {
            this.reportsRepoMaxUploadLimitMb = reportsRepoMaxUploadLimitMb;
        }

    }
}
