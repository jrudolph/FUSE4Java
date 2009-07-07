/**
 *   FUSE-J: Java bindings for FUSE (Filesystem in Userspace by Miklos Szeredi (mszeredi@inf.bme.hu))
 *
 *   Copyright (C) 2003 Peter Levart (peter@select-tech.si)
 *
 *   This program can be distributed under the terms of the GNU LGPL.
 *   See the file COPYING.LIB
 */

package fuse;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import fuse.impl.FilesystemToFuseFSAdapter;
import fuse.impl.FuseFS;


public class FuseMount
{
	private static final Log log = LogFactory.getLog(FuseMount.class);

	static class FuseLibrary {
		public FuseLibrary() {
			System.loadLibrary("fuse4java");
		}
	}
	@SuppressWarnings("unused")
	private final static FuseLibrary library = new FuseLibrary();

	private FuseMount()
	{
		// no instances
	}

	//
	// prefered String level API

	public static void mount(String[] args, Filesystem filesystem, Log log) throws Exception
	{ 
		mount(args, new FilesystemToFuseFSAdapter(filesystem, log));
	} 

	//
	// byte level API

	protected static void mount(String[] args, FuseFS fuseFS) throws Exception
	{
		ThreadGroup threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "FUSE Threads");
		threadGroup.setDaemon(true);

		log.info("Mounting filesystem");

		mount(args, fuseFS, threadGroup);

		log.info("Filesystem is unmounted");

		if (log.isDebugEnabled())
		{
			int n = threadGroup.activeCount();
			log.debug("ThreadGroup(\"" + threadGroup.getName() + "\").activeCount() = " + n);

			Thread[] threads = new Thread[n];
			threadGroup.enumerate(threads);
			for (int i = 0; i < threads.length; i++)
			{
				log.debug("thread[" + i + "] = " + threads[i] + ", isDaemon = " + threads[i].isDaemon());
			}
		}
	}


	private static native void mount(String[] args, FuseFS fuseFS, ThreadGroup threadGroup) throws Exception;
}
