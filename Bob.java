import java.net.*;
import java.util.Scanner;
import java.io.*;
public class Bob {
    public void Bob() {
	}
    private class WritingThread extends Thread {
        public WritingThread(){}
        String message;

        public void run(){
            try{
                Scanner scanner = new Scanner(System.in);
                // Prompt the user to enter port number
                System.out.print("Enter the port you want to connect: ");
                // Read the user's input as an integer
                int port = scanner.nextInt();
                Socket requestSocket = new Socket("localhost", port);
                System.out.println("Connected to localhost in port " + String.valueOf(port));
                DataOutputStream out = new DataOutputStream(requestSocket.getOutputStream());;         
                out.flush(); 

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                while(true)
                {
                    System.out.print("Hello, please input a command: ");
                    //read a sentence from the standard input
                    message = bufferedReader.readLine();
                    //Send the command to the server
                    sendMessage(message, out);
                    // split the command string into string array
                    String[] string_list= message.split(" ");
                    //send MESSAGE back to the client
                    if(string_list[0].equals("transfer")){
                        print("start transfering file to the other person...");
                        sendFile(string_list[1], out);
                    }
                }
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
            
        }
        
    }

    void sendMessage(String msg, DataOutputStream out) {
		try {
			out.writeUTF(msg);
			out.flush();
			System.out.println("Send message: " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
    void sendFile(String fileName, DataOutputStream out) {
        try {
            File file = new File(fileName);
            long fileSize = file.length();
            FileInputStream fileIn = new FileInputStream(file); // read file from local file
            byte[] buffer = new byte[1024];
            int bytes;

            out.writeLong(fileSize);
			out.flush();
            while ((bytes = fileIn.read(buffer)) != -1) { // fileIn.read(buffer) would return number of bytes read
                out.write(buffer, 0, bytes);
				out.flush();
            }

            fileIn.close();
            print("File sent: " + fileName);


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    void receiveFile(String fileName, long fileSize, DataInputStream in) {
        try {
            File file = new File("new"+fileName);
            FileOutputStream fileOut = new FileOutputStream(file); // output a new file
            byte[] buffer = new byte[1024];
            int bytes;
            long totalBytesRead = 0;

            while (totalBytesRead < fileSize && (bytes = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                fileOut.write(buffer, 0, bytes);
                totalBytesRead += bytes;
            }

            fileOut.close();
            System.out.println("File received: " + fileName);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void print(String string) {
		System.out.println(string);
	}
    void run()
	{			
		new WritingThread().start();
        ServerSocket sSocket;
        
        try{
            int sPort = 8001;
            sSocket = new ServerSocket(sPort, 10); // serversocket used to lisen on port number 8000
            System.out.println("The server is listening on port " + String.valueOf(sPort));
            System.out.println("Waiting for connection");
            Socket serverConnection = sSocket.accept();
            System.out.println("Connection received from " + serverConnection.getInetAddress().getHostName());
            DataOutputStream out = new DataOutputStream(serverConnection.getOutputStream());
            out.flush();
            DataInputStream in = new DataInputStream(serverConnection.getInputStream());
    
            while(true)
            {
                //receive the message sent from the client
                String message = in.readUTF();
                //show the message to the user
                System.out.println("Receive message: " + message);
                // split the command string into string array
                String[] string_list= message.split(" ");
                // address client's command
                if(string_list[0].equals("transfer")){
                    print("start receiving file from other person...");
                    long fileSize = in.readLong();
                    receiveFile(string_list[1], fileSize, in);
                }
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{

        }
        
       
            
           
        
 

	}

    public static void main(String args[]) {
		Bob bob = new Bob();
		bob.run();

	}
}
