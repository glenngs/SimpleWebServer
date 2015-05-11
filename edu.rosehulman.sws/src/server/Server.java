/*
 * Server.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */
 
package server;

import gui.WebServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * This represents a welcoming server for the incoming
 * TCP request from a HTTP client such as a web browser. 
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	
	private long connections;
	private long serviceTime;
	private long connectionsMissed;
	private File log;
	private HashMap<String,Integer> ipMap;
	
	protected RouteDispatcher dispatch;
	
	private WebServer window;
	/**
	 * @param rootDirectory
	 * @param port
	 * @throws IOException 
	 */
	public Server(String rootDirectory, int port, WebServer window) throws IOException {
		this.rootDirectory = rootDirectory;
		this.port = port;
		this.stop = false;
		this.connections = 0;
		this.serviceTime = 0;
		this.connectionsMissed = 0;
		this.window = window;
		this.log = new File(this.rootDirectory + "\\log.txt");
		ipMap = new HashMap<>();
		
		dispatch = new RouteDispatcher(rootDirectory);
		
		(new Thread(dispatch)).start();
	}

	public synchronized void addIpAddress(String ipAddress) {
		if (!ipMap.containsKey(ipAddress)) {
			ipMap.put(ipAddress, 0);
		}
		ipMap.put(ipAddress, ipMap.get(ipAddress) + 1);
	}
	
	public synchronized void removeIpAddress(String ipAddress) {
		if (ipMap.containsKey(ipAddress)) {
			if (ipMap.get(ipAddress) > 1) {
				ipMap.put(ipAddress, ipMap.get(ipAddress) - 1);
			} else {
				ipMap.remove(ipAddress);
			}
		}
	}
	
	public synchronized boolean ipAddressOverThreshold(String ipAddress) {
		if (ipMap.containsKey(ipAddress)) {
			return ipMap.get(ipAddress) > 10;
		}
		return false;
	}
	
	/**
	 * Gets the root directory for this web server.
	 * 
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}


	/**
	 * Gets the port number for this web server.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns connections serviced per second. 
	 * Synchronized to be used in threaded environment.
	 * 
	 * @return
	 */
	public synchronized double getServiceRate() {
		if(this.serviceTime == 0)
			return Long.MIN_VALUE;
		double rate = this.connections/(double)this.serviceTime;
		rate = rate * 1000;
		return rate;
	}
	
	public synchronized void appendToLog(String text) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.log, true), "utf-8"))) {
			   writer.write(text);
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
	}
	
	/**
	 * Increments number of connection by the supplied value.
	 * Synchronized to be used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementConnections(long value) {
		this.connections += value;
	}
	
	public synchronized void increamentConnectionsMissed() {
		this.connectionsMissed += 1;
		System.out.println("Out of memory missed " + this.connectionsMissed);
		System.out.println("Services made " + this.connections);
	}
	
	/**
	 * Increments the service time by the supplied value.
	 * Synchronized to be used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementServiceTime(long value) {
		this.serviceTime += value;
	}

	/**
	 * The entry method for the main server thread that accepts incoming
	 * TCP connection request and creates a {@link ConnectionHandler} for
	 * the request.
	 */
	public void run() {
		try {
			this.welcomeSocket = new ServerSocket(port);
			
			// Now keep welcoming new connections until stop flag is set to true
			while(true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();
				
				// Come out of the loop if the stop flag is set
				if(this.stop)
					break;
				
				// Create a handler for this incoming connection and start the handler in a new thread
				String ip = connectionSocket.getInetAddress().toString();
				if (!this.ipAddressOverThreshold(ip)) {
					this.addIpAddress(ip);
					ConnectionHandler handler = new ConnectionHandler(this, connectionSocket);
					new Thread(handler).start();
				} else {
					System.out.println("Ignored request");
					this.increamentConnectionsMissed();
				}
				
			}
			this.welcomeSocket.close();
		}
		catch(Exception e) {
			window.showSocketException(e);
		}
	}
	
	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if(this.stop)
			return;
		
		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept() method 
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), port);
			
			// We do not have any other job for this socket so just close it
			socket.close();
		}
		catch(Exception e){}
	}
	
	/**
	 * Checks if the server is stopeed or not.
	 * @return
	 */
	public boolean isStoped() {
		if(this.welcomeSocket != null)
			return this.welcomeSocket.isClosed();
		return true;
	}
}
