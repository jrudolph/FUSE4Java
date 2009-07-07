/**
 *   FUSE-J: Java bindings for FUSE (Filesystem in Userspace by Miklos Szeredi (mszeredi@inf.bme.hu))
 *
 *   Copyright (C) 2003 Peter Levart (peter@select-tech.si)
 *
 *   This program can be distributed under the terms of the GNU LGPL.
 *   See the file COPYING.LIB
 */
package fuse;

/**
 * Filesystem constants common to all filesystem interfaces
 */
public enum AccessMode {

	O_RDONLY(00), O_WRONLY(01), O_RDWR(02);

	AccessMode(int code) {
		this.code = code;
	}

	private final int code;

	public int getCode() {
		return code;
	}

}
