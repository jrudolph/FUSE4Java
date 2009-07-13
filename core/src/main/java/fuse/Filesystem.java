/**
 *   FUSE-J: Java bindings for FUSE (Filesystem in Userspace by Miklos Szeredi (mszeredi@inf.bme.hu))
 *
 *   Copyright (C) 2003 Peter Levart (peter@select-tech.si)
 *
 *   This program can be distributed under the terms of the GNU LGPL.
 *   See the file COPYING.LIB
 */
package fuse;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * The file system operations:
 *
 * Most of these should work very similarly to the well known UNIX
 * file system operations.  Exceptions are:
 *
 *  - All operations should return the error value (errno) by either:
 *     - throwing a fuse.FuseException with a errno field of the exception set to the desired fuse.Errno.E* value.
 *     - returning an integer value taken from fuse.Errno.E* constants.
 *       this is supposed to be less expensive in terms of CPU cycles and should only be used
 *       for very frequent errors (for example ENOENT).
 *
 *  - getdir() is the opendir(), readdir(), ..., closedir() sequence
 *  in one call.
 *
 *  - There is no create() operation, mknod() will be called for
 *  creation of all non directory, non symlink nodes.
 *
 *  - open() No
 *  creation, or trunctation flags (O_CREAT, O_EXCL, O_TRUNC) will be
 *  passed to open().  Open should only check if the operation is
 *  permitted for the given flags.
 *
 *  - read(), write(), release() are are passed a filehandle that is returned from open() in
 *  addition to a pathname.  The offset of the read and write is passed as the last
 *  argument, the number of bytes read/writen is returned through the java.nio.ByteBuffer object
 *
 *  - release() is called when an open file has:
 *       1) all file descriptors closed
 *       2) all memory mappings unmapped
 *    This call need only be implemented if this information is required.
 *
 *  - flush() called when a file is closed (can be called multiple times for each dup-ed filehandle)
 *
 *  - fsync() called when file data should be synced (with a flag to sync only data but not metadata)
 *
 */
public abstract class Filesystem
{
	public enum XAttrSetMode {
		XATTR_CREATE(0x1),
		XATTR_REPLACE(0x2);
		
		private final int flags;
		private XAttrSetMode(int flags) {
			this.flags = flags;
		}
		public int getFlags() {
			return flags;
		}
		
		public static XAttrSetMode byFlag(int flags) {
			for(XAttrSetMode mode : values()) {
				if(mode.getFlags() == flags) {
					return mode;
				}
			}
			throw new IllegalArgumentException("No valid set flag " + flags);
		}
	}
	
   public FuseAttr getattr(String path) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void readlink(String path, CharBuffer link) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void getdir(String path, FuseDirFiller dirFiller) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void mknod(String path, int mode, int rdev) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }
   
   public Object create(String path, int mode, int rdev) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void mkdir(String path, int mode) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void unlink(String path) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void rmdir(String path) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void symlink(String from, String to) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void rename(String from, String to) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void link(String from, String to) throws FuseException  {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void chmod(String path, int mode) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void chown(String path, int uid, int gid) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void truncate(String path, long size) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void utime(String path, int atime, int mtime) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public FuseStatfs statfs() throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   public void open(String path, FuseFileInfo ffi) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   // fh is filehandle passed from open
   public void read(String path, Object fh, ByteBuffer buf, long offset) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   // fh is filehandle passed from open,
   // isWritepage indicates that write was caused by a writepage
   public void write(String path, Object fh, boolean isWritepage, ByteBuffer buf, long offset) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   // called on every filehandle close, fh is filehandle passed from open
   public void flush(String path, Object fh) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   // called when last filehandle is closed, fh is filehandle passed from open
   public void release(String path, Object fh, int flags) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   // Synchronize file contents, fh is filehandle passed from open,
   // isDatasync indicates that only the user data should be flushed, not the meta data
   public void fsync(String path, Object fh, boolean isDatasync) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   /**
    * This method can be called to query for the size of the extended attribute
    *
    * @param path the path to file or directory containing extended attribute
    * @param name the name of the extended attribute
    * @param sizeSetter a callback interface that should be used to set the attribute's size
    * @return 0 if Ok or errno when error
    * @throws FuseException an alternative to returning errno is to throw this exception with errno initialized
    */
   public int getxattrsize(String path, String name) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   /**
    * This method will be called to get the value of the extended attribute
    *
    * @param path the path to file or directory containing extended attribute
    * @param name the name of the extended attribute
    * @param dst a ByteBuffer that should be filled with the value of the extended attribute
    * @return 0 if Ok or errno when error
    * @throws FuseException an alternative to returning errno is to throw this exception with errno initialized
    * @throws BufferOverflowException should be thrown to indicate that the given <code>dst</code> ByteBuffer
    *         is not large enough to hold the attribute's value. After that <code>getxattr()</code> method will
    *         be called again with a larger buffer.
    */
   public void getxattr(String path, String name, ByteBuffer dst) throws FuseException, BufferOverflowException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   /**
    * This method will be called to get the list of extended attribute names
    *
    * @param path the path to file or directory containing extended attributes
    * @param lister a callback interface that should be used to list the attribute names
    * @return 0 if Ok or errno when error
    * @throws FuseException an alternative to returning errno is to throw this exception with errno initialized
    */
   public void listxattr(String path, XattrLister lister) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   /**
    * This method will be called to set the value of an extended attribute
    *
    * @param path the path to file or directory containing extended attributes
    * @param name the name of the extended attribute
    * @param value the value of the extended attribute
    * @param flags parameter can be used to refine the semantics of the operation.<p>
    *        <code>XATTR_CREATE</code> specifies a pure create, which should fail with <code>Errno.EEXIST</code> if the named attribute exists already.<p>
    *        <code>XATTR_REPLACE</code> specifies a pure replace operation, which should fail with <code>Errno.ENOATTR</code> if the named attribute does not already exist.<p>
    *        By default (no flags), the  extended  attribute  will  be created if need be, or will simply replace the value if the attribute exists.
    * @return 0 if Ok or errno when error
    * @throws FuseException an alternative to returning errno is to throw this exception with errno initialized
    */
   public void setxattr(String path, String name, ByteBuffer value, XAttrSetMode flags) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }

   /**
    * This method will be called to remove the extended attribute
    *
    * @param path the path to file or directory containing extended attributes
    * @param name the name of the extended attribute
    * @return 0 if Ok or errno when error
    * @throws FuseException an alternative to returning errno is to throw this exception with errno initialized
    */
   public void removexattr(String path, String name) throws FuseException {
	   throw FuseException.fromErrno(Errno.ENOTSUPP);
   }
   
   public Charset getPathCharSet() {
	   String encoding = System.getProperty("file.encoding", "UTF-8");
	   return Charset.forName(encoding);
   }
}
