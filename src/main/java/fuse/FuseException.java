/**
 *   FUSE-J: Java bindings for FUSE (Filesystem in Userspace by Miklos Szeredi (mszeredi@inf.bme.hu))
 *
 *   Copyright (C) 2003 Peter Levart (peter@select-tech.si)
 *
 *   This program can be distributed under the terms of the GNU LGPL.
 *   See the file COPYING.LIB
 */

package fuse;

public class FuseException extends Exception {
	private static final long serialVersionUID = 0;

	private Errno errno;

	public FuseException(Errno errno) {
		this(errno, errno.getMsg());
	}

	public FuseException(String message) {
		this(Errno.EIO, message);
	}
	
	public FuseException(Errno errno, String message) {
		this(errno, message, null);
	}

	public FuseException(String message, Throwable cause) {
		this(Errno.EIO, message, cause);
	}
	
	public FuseException(Errno errno, String message, Throwable cause) {
		super(message, cause);
		this.errno = errno;
	}

	public Errno getErrno() {
		return errno;
	}
	
	public static FuseException fromErrno(Errno errno) {
		return new FuseException(errno);
	}
}
