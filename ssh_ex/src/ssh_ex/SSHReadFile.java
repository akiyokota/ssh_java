package ssh_ex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;



/**
 *
 * @author World
 */
public class SSHReadFile
    {
    public static String user = "root";
    public static String password = "hadoop";
    public static String host = "10.28.144.82";
    public static int port=22;
    public static JSch jsch = new JSch();
    public static Session session; 
    public static ChannelSftp sftpChannel;
    
    public static void init() {
    	try
        {
	        session = jsch.getSession(user, host, port);
	        session.setPassword(password);
	        session.setConfig("StrictHostKeyChecking", "no");
	        
	        System.out.println("Establishing Connection...");
	        session.connect();
	        System.out.println("Connection established.");
	        
	    
	        System.out.println("Crating SFTP Channel.");
	        sftpChannel = (ChannelSftp) session.openChannel("sftp");
	        sftpChannel.connect();
	        System.out.println("SFTP Channel created.");
	        System.out.println("--------------------------------------------------");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    
    public static void readFile(String filename) {
    	//below code can read a file
        try {
	        InputStream out= null;
	        out= sftpChannel.get(filename);
	        BufferedReader br = new BufferedReader(new InputStreamReader(out));
	        String line;
	        while ((line = br.readLine()) != null)
	            System.out.println(line);
	        br.close();
	        out.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }
    
    public static void executeCommand(String cmd) {
    	try {
	    	StringBuilder outputBuffer = new StringBuilder();
	        Channel channel = session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(cmd);
	        
	        
	        InputStream commandOutput = channel.getInputStream();
	        channel.connect();
	        int readByte = commandOutput.read();
	
	        while(readByte != 0xffffffff)
	        {
	           outputBuffer.append((char)readByte);
	           readByte = commandOutput.read();
	        }
	        System.out.println(outputBuffer);
	        channel.disconnect();

	        
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        
    }
    
    public static void shutdown() {
        session.disconnect();
        sftpChannel.disconnect();
    }
    
    public static void main(String args[])
    {
	    try {
	    	init();
	    	
	    	Configuration configuration = new Configuration();
	    	//FileSystem hdfs = FileSystem.get( new URI( "hdfs://sandbox:8020/apps/hive/warehouse/sample_01/000000_0" ), configuration );
	    	FileSystem hdfs = FileSystem.get( new URI( "hdfs://10.28.144.136:8020", "root", "hadoop" ), configuration );
	    	Path file = new Path("hdfs://sandbox:8020/apps/hive/warehouse/sample_01/000000_0");
	    	OutputStream os = hdfs.append(file);
	    	BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
	    	br.append("555/u0001string555");
	    	br.close();
	        //executeCommand("hadoop fs -appendToFile 30\u0001string30 hdfs://sandbox:8020/apps/hive/warehouse/sample_01/000000_0");
	        //readFile("ifconfig.txt");
	        shutdown();
	    } catch(Exception e){
		    	System.err.print(e);
		}
    }
}