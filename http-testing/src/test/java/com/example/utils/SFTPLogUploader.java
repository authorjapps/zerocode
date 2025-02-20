package com.example.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;


public class SFTPLogUploader {

    private static final String SFTP_HOST = PropertiesReader.getProperty("sftp.host");
    private static final int SFTP_PORT = PropertiesReader.getIntProperty("sftp.port");
    private static final String SFTP_USER = PropertiesReader.getProperty("sftp.user");
    private static final String SFTP_PASS = PropertiesReader.getProperty("sftp.password");
    private static final String SFTP_REMOTE_DIR = PropertiesReader.getProperty("sftp.remoteDir");
    private static final String LOCAL_LOG_FILE = PropertiesReader.getProperty("sftp.localFile");


    public static void uploadLogFile() {
        Session session = null;
        ChannelSftp sftpChannel = null;
        FileInputStream inputStream = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTP_USER, SFTP_HOST, SFTP_PORT);
            session.setPassword(SFTP_PASS);

            // Ignore SSH host key checking (for testing purposes)
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            System.out.println("Connecting to SFTP server..."); //Notify User
            session.connect();

            // Open Channel
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            System.out.println("Connected to SFTP. Uploading file..."); //Notify User

            File logFile = new File(LOCAL_LOG_FILE);
            if (!logFile.exists()) {
                //Unable to find the log file.
                System.out.println("Log file does not exist: " + logFile.getAbsolutePath());
                return;
            }

            inputStream = new FileInputStream(logFile);
            String remoteFilePath = SFTP_REMOTE_DIR + "zerocode_execution.log";
            sftpChannel.put(inputStream, remoteFilePath);

            //Success!
            System.out.println("Log file successfully uploaded to SFTP: " + remoteFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Close all of our connections.
            try {
                if (inputStream != null) inputStream.close();
                if (sftpChannel != null) sftpChannel.exit();
                if (session != null) session.disconnect();
                System.out.println("SFTP session closed.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        uploadLogFile();
    }
}
