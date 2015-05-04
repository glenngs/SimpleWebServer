/*
 * RouteDispatcher.java
 * May 4, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
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
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 

package server;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class RouteDispatcher implements Runnable {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private Map<String, IHttpServlet> routes;
	private String rootDir;

	/**
	 * @throws IOException
	 * 
	 */
	public RouteDispatcher(String root) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.routes = new HashMap<String, IHttpServlet>();
		this.rootDir = root;

		File path = new File(this.rootDir);
		grabPlugins();
		registerPath(path.toPath());
	}
	
	public void grabPlugins() throws IOException {
		File dir = new File(this.rootDir);
		System.out.println(dir.getAbsolutePath());
		// If directory doesn't exist, create it.
		if (!dir.exists()) {
			dir.mkdir();
		}
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		if (files != null) {
			for (File jarfile : files) {
				addRoutes(jarfile);
				System.out.println(jarfile.getName());
			}
		}
	}

	private void registerPath(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);
		keys.put(key, dir);
	}

	public void dispatchRoute(HttpRequest request, HttpResponse response) throws NullPointerException {
		System.out.println(request.getHeader().get("testWord"));
		this.routes.get(request.getUri()).handleResponse(request, response);
	}


	public List<String> scanJarFileForClasses(File file) throws IOException,
			IllegalArgumentException {
		if (file == null || !file.exists())
			throw new IllegalArgumentException(
					"Invalid jar-file to scan provided");
		List<String> foundClasses = new ArrayList<String>();
		if (file.getName().endsWith(".jar")) {
			
			try (JarFile jarFile = new JarFile(file)) {
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().endsWith(".class")) {
						String name = entry.getName();
						name = name.substring(0, name.lastIndexOf(".class"));
						if (name.indexOf("/") != -1)
							name = name.replaceAll("/", ".");
						if (name.indexOf("\\") != -1)
							name = name.replaceAll("\\", ".");
						foundClasses.add(name);
					}
				}
			}
			
		}
		return foundClasses;
	}

	public List<Class<?>> findImplementingClassesInJarFile(File file,
			Class<?> iface, ClassLoader loader) throws Exception {
		List<Class<?>> implementingClasses = new ArrayList<Class<?>>();
		// scan the jar file for all included classes
		for (String classFile : scanJarFileForClasses(file)) {
			Class<?> clazz;
			try {
				// now try to load the class
				if (loader == null)
					clazz = Class.forName(classFile);
				else
					clazz = Class.forName(classFile, true, loader);

				// and check if the class implements the provided interface
				if (iface.isAssignableFrom(clazz) && !clazz.equals(iface))
					implementingClasses.add(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return implementingClasses;
	}

	public void addRoutes(File downloadedJarFile) throws MalformedURLException
	{
	    if (downloadedJarFile == null || !downloadedJarFile.exists())
	        throw new IllegalArgumentException("Invalid jar file provided");

	    URL downloadURL = downloadedJarFile.toURI().toURL();
	    URL[] downloadURLs = new URL[] { downloadURL };
	    URLClassLoader loader = URLClassLoader.newInstance(downloadURLs, getClass().getClassLoader());
	    try
	    {
	        List<Class<?>> implementingClasses = findImplementingClassesInJarFile(downloadedJarFile, IHttpServlet.class, loader);
	        for (Class<?> clazz : implementingClasses)
	        {
	            // assume there is a public default constructor available
	            IHttpServlet instance = (IHttpServlet) clazz.newInstance();
	            // ... do whatever you like here
	            routes.put("/" + stripExtension(downloadedJarFile.getName()) + "/" + clazz.getName(),instance);
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	public void removeRoutes(File jarFile) {
		for(Iterator<Map.Entry<String, IHttpServlet>> it = routes.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, IHttpServlet> entry = it.next();
			if (entry.getKey().startsWith("/" + jarFile.getName())) {
				it.remove();
			}
		}
	}

	static String stripExtension(String str) {
		if (str == null)
			return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1)
			return str;
		return str.substring(0, pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */

	@Override
	public void run() {
		for (;;) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();

				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path name = ev.context();

				File jarfile = new File(dir.toString(), name.toString());

				if (kind == ENTRY_CREATE) {
					try {
						addRoutes(jarfile);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else if (kind == ENTRY_DELETE) {
					removeRoutes(jarfile);
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}

	}

}
