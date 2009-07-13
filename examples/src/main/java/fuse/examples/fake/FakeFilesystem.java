/**
 *   FUSE-J: Java bindings for FUSE (Filesystem in Userspace by Miklos Szeredi (mszeredi@inf.bme.hu))
 *
 *   Copyright (C) 2003 Peter Levart (peter@select-tech.si)
 *
 *   This program can be distributed under the terms of the GNU LGPL.
 *   See the file COPYING.LIB
 */

package fuse.examples.fake;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fuse.Errno;
import fuse.Filesystem;
import fuse.FuseAttr;
import fuse.FuseDirFiller;
import fuse.FuseException;
import fuse.FuseFileInfo;
import fuse.FuseMount;
import fuse.FuseStatfs;
import fuse.Mode;
import fuse.XattrLister;
import fuse.impl.FuseSizeSetter;

public class FakeFilesystem extends Filesystem 
{
	private static final Log log = LogFactory.getLog(FakeFilesystem.class);

	private static final int BLOCK_SIZE = 512;
	private static final int NAME_LENGTH = 1024;

	private static class Link extends Node
	{
		String link;

		Link(String name, int mode, String link, String ... xattrs)
		{
			super(name, mode, xattrs);

			this.link = link;
		}
	}

	private static class FH
	{
		Node n;

		FH(Node n)
		{
			this.n = n;
			log.debug("  " + this + " created");
		}

		void release()
		{
			log.debug("  " + this + " released");
		}

		protected void finalize()
		{
			log.debug("  " + this + " finalized");
		}

		public String toString()
		{
			return "FH[" + n + ", hashCode=" + hashCode() + "]";
		}
	}


	// a root directory
	private Directory root;


	// lookup node
	private Node lookup(String path)
	{
		if (path.equals("/"))
			return root;

		java.io.File f = new java.io.File(path);
		Node parent = lookup(f.getParent());
		Node node = (parent instanceof Directory)? ((Directory) parent).getFiles().get(f.getName()) : null;

		if (log.isDebugEnabled())
			log.debug("  lookup(\"" + path + "\") returning: " + node);

		return node;
	}


	public FakeFilesystem()
	{
		root = new Directory("", 0755, "description", "ROOT directory");

		root.add(new File("README", 0644, "Hou have read me\n", "mimetype", "text/plain", "description", "a README file"));
		root.add(new File("execute_me.sh", 0755, "#!/bin/sh\n\necho \"You executed me\"\n", "mimetype", "text/plain", "description", "a BASH script"));

		Directory subdir = new Directory("subdir", 0755, "description", "a subdirectory");
		root.add(subdir);
		subdir.add(new Link("README.link", 0666, "../README", "description", "a symbolic link"));
		subdir.add(new Link("execute_me.link.sh", 0666, "../execute_me.sh", "description", "another symbolic link"));

		log.info("created");
	}


	public void chmod(String path, int mode) throws FuseException
	{
		Node n = lookup(path);

		if (n == null)
		{
			throw FuseException.fromErrno(Errno.ENOENT);
		}
		n.setMode((n.getMode() & Mode.TYPE_MASK.getMode()) | (mode & Mode.PERMISSION_MASK.getMode()));
	}

	public void chown(String path, int uid, int gid) throws FuseException
	{
		return;
	}

	public FuseAttr getattr(String path) throws FuseException
	{
		Node n = lookup(path);

		int time = (int) (System.currentTimeMillis() / 1000L);

		if (n instanceof Directory)
		{
			Directory d = (Directory) n;
			return FuseAttr.newBuilder()
			.inode(d.hashCode())
			.mode(Mode.TYPE_DIR)
			.mode(d.getMode())
			.nlink(1)
			.size(d.getFiles().size() * NAME_LENGTH)
			.blocks((d.getFiles().size() * NAME_LENGTH + BLOCK_SIZE - 1) / BLOCK_SIZE)
			.mtime(time)
			.ctime(time)
			.atime(time).build();
		}
		else if (n instanceof File)
		{
			File f = (File) n;
			return FuseAttr.newBuilder()
			.inode(f.hashCode())
			.mode(Mode.TYPE_FILE)
			.mode(f.getMode())
			.nlink(1)
			.size(f.getContent().length)
			.blocks((f.getContent().length + BLOCK_SIZE - 1) / BLOCK_SIZE)
			.mtime(time)
			.ctime(time)
			.atime(time).build();
		}
		else if (n instanceof Link)
		{
			Link l = (Link) n;
			return FuseAttr.newBuilder()
			.inode(l.hashCode())
			.mode(Mode.TYPE_SYMLINK)
			.mode(l.getMode())
			.nlink(1)
			.size(l.link.length())
			.blocks((l.link.length() + BLOCK_SIZE - 1) / BLOCK_SIZE)
			.blocks(1)
			.build();
		}
		throw FuseException.fromErrno(Errno.ENOENT);
	}

	public void getdir(String path, FuseDirFiller filler) throws FuseException
	{
		Node n = lookup(path);

		if (n instanceof Directory)
		{
			Directory d = (Directory) n;
			for (Node child : d.getFiles().values())
			{
				Mode ftype = (child instanceof Directory)
				? Mode.TYPE_DIR
						: ((child instanceof File)
								? Mode.TYPE_FILE
										: ((child instanceof Link)
												? Mode.TYPE_SYMLINK
														: null));
				if (ftype != null)
					filler.add(
							child.getName(),
							child.hashCode(),
							ftype.getMode() | child.getMode()
					);
			}
		}
		throw FuseException.fromErrno(Errno.ENOENT);
	}

	public void link(String from, String to) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public void mkdir(String path, int mode) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public void mknod(String path, int mode, int rdev) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public void rename(String from, String to) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public void rmdir(String path) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public FuseStatfs statfs() throws FuseException
	{
		return FuseStatfs.newBuilder().blockSize(BLOCK_SIZE).blocks(1000).blocksFree(200).blocksAvail(180).files(Node.getFileCount()).
		filesFree(0).namelen(NAME_LENGTH).build();
	}

	public void symlink(String from, String to) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public void truncate(String path, long size) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public void unlink(String path) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	public void utime(String path, int atime, int mtime) throws FuseException {
	}

	public void readlink(String path, CharBuffer link) throws FuseException
	{
		Node n = lookup(path);

		if(!(n instanceof Link)) {
			throw FuseException.fromErrno(Errno.ENOENT);
		}
		Link l = (Link) n;
		link.append(l.link);
	}

	// if open returns a filehandle by calling FuseOpenSetter.setFh() method, it will be passed to every method that supports 'fh' argument
	public void open(String path, int flags, FuseFileInfo ffi) throws FuseException
	{
		Node n = lookup(path);
		if(n == null) {
			throw FuseException.fromErrno(Errno.ENOENT);
		}

		ffi.setFh(new FH(n));
	}

	// fh is filehandle passed from open,
	// isWritepage indicates that write was caused by a writepage
	public void write(String path, Object fh, boolean isWritepage, ByteBuffer buf, long offset) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	// fh is filehandle passed from open
	public void read(String path, Object fh, ByteBuffer buf, long offset) throws FuseException
	{
		if (fh instanceof FH)
		{
			File f = (File) ((FH) fh).n;
			buf.put(f.getContent(), (int) offset, Math.min(buf.remaining(), f.getContent().length - (int)offset));
			return;
		}

		throw FuseException.fromErrno(Errno.EBADF);
	}

	// new operation (called on every filehandle close), fh is filehandle passed from open
	public void flush(String path, Object fh) throws FuseException
	{
		if (fh instanceof FH)
			return;

		throw FuseException.fromErrno(Errno.EBADF);
	}

	// (called when last filehandle is closed), fh is filehandle passed from open
	public void release(String path, Object fh, int flags) throws FuseException
	{
		if (fh instanceof FH)
		{
			((FH) fh).release();
			System.runFinalization();
			return;
		}

		throw FuseException.fromErrno(Errno.EBADF);
	}

	//
	// XattrSupport implementation

	/**
	 * This method will be called to get the value of the extended attribute
	 *
	 * @param path the path to file or directory containing extended attribute
	 * @param name the name of the extended attribute
	 * @param dst  a ByteBuffer that should be filled with the value of the extended attribute
	 * @return 0 if Ok or errno when error
	 * @throws fuse.FuseException an alternative to returning errno is to throw this exception with errno initialized
	 * @throws java.nio.BufferOverflowException
	 *                            should be thrown to indicate that the given <code>dst</code> ByteBuffer
	 *                            is not large enough to hold the attribute's value. After that <code>getxattr()</code> method will
	 *                            be called again with a larger buffer.
	 */
	public void getxattr(String path, String name, ByteBuffer dst) throws FuseException, BufferOverflowException
	{
		Node n = lookup(path);

		if (n == null)
			throw FuseException.fromErrno(Errno.ENOENT);

		byte[] value = n.getXAttr().get(name);

		if (value == null)
			throw FuseException.fromErrno(Errno.ENOATTR);

		dst.put(value);
	}

	/**
	 * This method can be called to query for the size of the extended attribute
	 *
	 * @param path       the path to file or directory containing extended attribute
	 * @param name       the name of the extended attribute
	 * @param sizeSetter a callback interface that should be used to set the attribute's size
	 * @return 0 if Ok or errno when error
	 * @throws fuse.FuseException an alternative to returning errno is to throw this exception with errno initialized
	 */
	public int getxattrsize(String path, String name, FuseSizeSetter sizeSetter) throws FuseException
	{
		Node n = lookup(path);

		if (n == null)
			throw FuseException.fromErrno(Errno.ENOENT);

		byte[] value = n.getXAttr().get(name);

		if (value == null)
			throw FuseException.fromErrno(Errno.ENOATTR);

		sizeSetter.setSize(value.length);

		return 0;
	}

	/**
	 * This method will be called to get the list of extended attribute names
	 *
	 * @param path   the path to file or directory containing extended attributes
	 * @param lister a callback interface that should be used to list the attribute names
	 * @return 0 if Ok or errno when error
	 * @throws fuse.FuseException an alternative to returning errno is to throw this exception with errno initialized
	 */
	public void listxattr(String path, XattrLister lister) throws FuseException
	{
		Node n = lookup(path);

		if (n == null)
			throw FuseException.fromErrno(Errno.ENOENT);

		for (String xattrName : n.getXAttr().keySet())
			lister.add(xattrName);
	}

	/**
	 * This method will be called to remove the extended attribute
	 *
	 * @param path the path to file or directory containing extended attributes
	 * @param name the name of the extended attribute
	 * @return 0 if Ok or errno when error
	 * @throws fuse.FuseException an alternative to returning errno is to throw this exception with errno initialized
	 */
	public void removexattr(String path, String name) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}

	/**
	 * This method will be called to set the value of an extended attribute
	 *
	 * @param path  the path to file or directory containing extended attributes
	 * @param name  the name of the extended attribute
	 * @param value the value of the extended attribute
	 * @param flags parameter can be used to refine the semantics of the operation.<p>
	 *              <code>XATTR_CREATE</code> specifies a pure create, which should fail with <code>Errno.EEXIST</code> if the named attribute exists already.<p>
	 *              <code>XATTR_REPLACE</code> specifies a pure replace operation, which should fail with <code>Errno.ENOATTR</code> if the named attribute does not already exist.<p>
	 *              By default (no flags), the  extended  attribute  will  be created if need be, or will simply replace the value if the attribute exists.
	 * @return 0 if Ok or errno when error
	 * @throws fuse.FuseException an alternative to returning errno is to throw this exception with errno initialized
	 */
	public int setxattr(String path, String name, ByteBuffer value, int flags) throws FuseException
	{
		throw FuseException.fromErrno(Errno.EROFS);
	}


	//
	// Java entry point

	public static void main(String[] args)
	{
		log.info("entering");

		try
		{
			FuseMount.mount(args, new FakeFilesystem(), log);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			log.info("exiting");
		}
	}
}
