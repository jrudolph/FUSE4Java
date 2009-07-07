/**
 *   FUSE-J: Java bindings for FUSE (Filesystem in Userspace by Miklos Szeredi (mszeredi@inf.bme.hu))
 *
 *   Copyright (C) 2003 Peter Levart (peter@select-tech.si)
 *
 *   This program can be distributed under the terms of the GNU LGPL.
 *   See the file COPYING.LIB
 */
package fuse.impl;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Date;

import org.apache.commons.logging.Log;

import fuse.Errno;
import fuse.Filesystem;
import fuse.FuseAttr;
import fuse.FuseDirFiller;
import fuse.FuseException;
import fuse.FuseFileInfo;
import fuse.FuseStatfs;
import fuse.XattrLister;
import fuse.Filesystem.XAttrSetMode;
import fuse.impl.util.FilesystemImplCheck;

/**
 * This is an adapter that implements fuse.FuseFS byte level API and delegates
 * to the fuse.Filesystem3 String level API. You specify the encoding to be used
 * for file names and paths.
 */
public class FilesystemToFuseFSAdapter implements FuseFS
{
   private Filesystem fs;
   private Charset cs;
   private Log log;

   public FilesystemToFuseFSAdapter(Filesystem fs, Log log)
   {
      this.fs= fs;
      this.cs = fs.getPathCharSet();
      this.log = log;
   }

   //
   // FuseFS implementation

   public int getattr(ByteBuffer path, FuseGetattrSetter getattrSetter)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("getattr: path=" + pathStr);

      try
      {
         FuseAttr attr = fs.getattr(pathStr);
         if(attr == null) {
        	 return Errno.ENOENT.getErrno();
         }
         int mode = attr.getMode();
         getattrSetter.set(attr.getInode(), mode, attr.getNlink(), attr.getUid(), attr.getGid(), attr.getRdev(), 
        		 attr.getSize(), attr.getBlocks(), attr.getAtime(), attr.getMtime(), attr.getCtime());
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int readlink(ByteBuffer path, ByteBuffer link)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("readlink: path=" + pathStr);

      CharBuffer linkCb = CharBuffer.allocate(link.capacity());

      try
      {
         fs.readlink(pathStr, linkCb);

            linkCb.flip();

            CharsetEncoder enc = cs.newEncoder()
               .onUnmappableCharacter(CodingErrorAction.REPLACE)
               .onMalformedInput(CodingErrorAction.REPLACE);

            CoderResult result = enc.encode(linkCb, link, true);
            if (result.isOverflow())
               throw new FuseException(Errno.ENAMETOOLONG, "Buffer owerflow while encoding result");

         return handleErrno(0, linkCb.rewind());
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   class FuseDirFillerAdapter implements FuseDirFiller {
	   private final FuseFSDirFiller df;

	   FuseDirFillerAdapter(FuseFSDirFiller df) {
		   this.df = df;
	   }
	   
	@Override
	public void add(String name, long inode, int mode) {
		df.add(name, inode, mode);
	}
   }

   public int getdir(ByteBuffer path, FuseFSDirFiller dirFiller)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("getdir: path=" + pathStr);

      try
      {
         dirFiller.setCharset(cs);
         fs.getdir(pathStr, new FuseDirFillerAdapter(dirFiller)); 
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int mknod(ByteBuffer path, int mode, int rdev)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("mknod: path=" + pathStr + ", mode=" + Integer.toOctalString(mode) + "(OCT), rdev=" + rdev);

      try
      {
         fs.mknod(pathStr, mode, rdev);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int mkdir(ByteBuffer path, int mode)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("mkdir: path=" + pathStr + ", mode=" + Integer.toOctalString(mode) + "(OCT)");

      try
      {
          fs.mkdir(pathStr, mode);
          return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int unlink(ByteBuffer path)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("unlink: path=" + pathStr);

      try
      {
    	  fs.unlink(pathStr);
    	  return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int rmdir(ByteBuffer path)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("rmdir: path=" + pathStr);

      try
      {
    	  fs.rmdir(pathStr);
    	  return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int symlink(ByteBuffer from, ByteBuffer to)
   {
      String fromStr = cs.decode(from).toString();
      String toStr = cs.decode(to).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("symlink: from=" + fromStr + " to=" + toStr);

      try
      {
         fs.symlink(fromStr, toStr);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int rename(ByteBuffer from, ByteBuffer to)
   {
      String fromStr = cs.decode(from).toString();
      String toStr = cs.decode(to).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("rename: from=" + fromStr + " to=" + toStr);

      try
      {
         fs.rename(fromStr, toStr);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int link(ByteBuffer from, ByteBuffer to)
   {
      String fromStr = cs.decode(from).toString();
      String toStr = cs.decode(to).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("link: from=" + fromStr + " to=" + toStr);

      try
      {
         fs.link(fromStr, toStr);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int chmod(ByteBuffer path, int mode)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("chmod: path=" + pathStr + ", mode=" + Integer.toOctalString(mode) + "(OCT)");

      try
      {
         fs.chmod(pathStr, mode);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int chown(ByteBuffer path, int uid, int gid)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("chown: path=" + pathStr + ", uid=" + uid + ", gid=" + gid);

      try
      {
         fs.chown(pathStr, uid, gid);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int truncate(ByteBuffer path, long size)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("truncate: path=" + pathStr + ", size=" + size);

      try
      {
         fs.truncate(pathStr, size);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int utime(ByteBuffer path, int atime, int mtime)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("utime: path=" + pathStr + ", atime=" + atime + " (" + new Date((long)atime * 1000L) + "), mtime=" + mtime + " (" + new Date((long)mtime * 1000L) + ")");

      try
      {
         fs.utime(pathStr, atime, mtime);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int statfs(FuseStatfsSetter statfsSetter)
   {
      if (log != null && log.isDebugEnabled())
         log.debug("statfs");

      try
      {
    	  FuseStatfs statfs = fs.statfs();
    	  statfsSetter.set(statfs.getBlockSize(), statfs.getBlocks(), statfs.getBlocksFree(), statfs.getBlocksAvail(), statfs.getFiles(), statfs.getFilesFree(), statfs.getNamelen());
    	  return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int open(ByteBuffer path, int flags, FuseOpenSetter openSetter)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("open: path=" + pathStr + ", flags=" + flags);

      try
      {
    	  FuseFileInfo ffi = new FuseFileInfo(openSetter.isDirectIO(), openSetter.isKeepCache(), flags, null);
         fs.open(pathStr, ffi);
         openSetter.setFh(ffi.getFh());
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int read(ByteBuffer path, Object fh, ByteBuffer buf, long offset)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("read: path=" + pathStr + ", fh=" + fh + ", offset=" + offset);

      try
      {
         fs.read(pathStr, fh, buf, offset);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int write(ByteBuffer path, Object fh, boolean isWritepage, ByteBuffer buf, long offset)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("write: path=" + pathStr + ", fh=" + fh + ", isWritepage=" + isWritepage + ", offset=" + offset);

      try
      {
         fs.write(pathStr, fh, isWritepage, buf, offset);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int flush(ByteBuffer path, Object fh)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("flush: path=" + pathStr + ", fh=" + fh);

      try
      {
         fs.flush(pathStr, fh);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int release(ByteBuffer path, Object fh, int flags)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("release: path=" + pathStr + ", fh=" + fh + ", flags=" + flags);

      try
      {
         fs.release(pathStr, fh, flags);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }


   public int fsync(ByteBuffer path, Object fh, boolean isDatasync)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("fsync: path=" + pathStr + ", fh=" + fh + ", isDatasync=" + isDatasync);

      try
      {
         fs.fsync(cs.decode(path).toString(), fh, isDatasync);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   //
   // extended attribute support is optional

   public int getxattrsize(ByteBuffer path, ByteBuffer name, FuseSizeSetter sizeSetter)
   {
      String pathStr = cs.decode(path).toString();
      String nameStr = cs.decode(name).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("getxattrsize: path=" + pathStr + ", name=" + nameStr);

      try
      {
         int size = fs.getxattrsize(pathStr, nameStr);
         sizeSetter.setSize(size);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   public int getxattr(ByteBuffer path, ByteBuffer name, ByteBuffer value)
   {
      String pathStr = cs.decode(path).toString();
      String nameStr = cs.decode(name).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("getxattr: path=" + pathStr + ", name=" + nameStr);

      try
      {
         fs.getxattr(pathStr, nameStr, value);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   //
   // private implementation of XattrLister that estimates the byte size of the attribute names list
   // using Charset of the enclosing Filesystem3ToFuseFSAdapter class

   private class XattrSizeLister implements XattrLister
   {
      CharsetEncoder enc = cs.newEncoder();
      int size = 0;

      public void add(String xattrName)
      {
         size += (int) ((float) xattrName.length() * enc.averageBytesPerChar()) + 1;
      }
   }

   //
   // estimate the byte size of attribute names list...

   public int listxattrsize(ByteBuffer path, FuseSizeSetter sizeSetter)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("listxattrsize: path=" + pathStr);

      XattrSizeLister lister = new XattrSizeLister();

      try
      {
         fs.listxattr(pathStr, lister);
         sizeSetter.setSize(lister.size);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   //
   // private implementation of XattrLister that encodes list of attribute names into given ByteBuffer
   // using Charset of the enclosing Filesystem3ToFuseFSAdapter class

   private class XattrValueLister implements XattrLister
   {
      CharsetEncoder enc = cs.newEncoder()
         .onMalformedInput(CodingErrorAction.REPLACE)
         .onUnmappableCharacter(CodingErrorAction.REPLACE);
      ByteBuffer list;

      XattrValueLister(ByteBuffer list)
      {
         this.list = list;
      }

      public void add(String xattrName)
      {
               enc.encode(CharBuffer.wrap(xattrName), list, true);
               list.put((byte) 0); // each attribute name is terminated by byte 0
      }

      //
      // for debugging

      public String toString()
      {
         StringBuilder sb = new StringBuilder();

         sb.append("[");
         boolean first = true;

         for (int i = 0; i < list.position(); i++)
         {
            int offset = i;
            int length = 0;
            while (offset + length < list.position() && list.get(offset + length) != 0)
               length++;

            byte[] nameBytes = new byte[length];
            for (int j = 0; j < length; j++)
               nameBytes[j] = list.get(offset + j);

            if (first)
               first = false;
            else
               sb.append(", ");

            sb.append('"').append(cs.decode(ByteBuffer.wrap(nameBytes))).append('"');

            i = offset + length;
         }

         sb.append("]");

         return sb.toString();
      }
   }

   //
   // list attributes into given ByteBuffer...

   public int listxattr(ByteBuffer path, final ByteBuffer list)
   {
      String pathStr = cs.decode(path).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("listxattr: path=" + pathStr);
      XattrValueLister lister = new XattrValueLister(list);

      try
      {
         fs.listxattr(pathStr, lister);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   public int setxattr(ByteBuffer path, ByteBuffer name, ByteBuffer value, int flags)
   {
      String pathStr = cs.decode(path).toString();
      String nameStr = cs.decode(name).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("setxattr: path=" + pathStr + ", name=" + nameStr + ", value=" + value + ", flags=" + flags);

      try
      {
    	 XAttrSetMode attrFlag = XAttrSetMode.byFlag(flags);
         fs.setxattr(pathStr, nameStr, value, attrFlag);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   public int removexattr(ByteBuffer path, ByteBuffer name)
   {
      String pathStr = cs.decode(path).toString();
      String nameStr = cs.decode(name).toString();

      if (log != null && log.isDebugEnabled())
         log.debug("removexattr: path= " + pathStr + ", name=" + nameStr);

      try
      {
         fs.removexattr(pathStr, nameStr);
         return 0;
      }
      catch (Exception e)
      {
         return handleException(e);
      }
   }

   //
   // private

   private int handleErrno(int errno)
   {
      if (log != null && log.isDebugEnabled())
         log.debug((errno == 0)? "  returning with success" : "  returning errno: " + errno);

      return errno;
   }

   private int handleErrno(int errno, Object v1)
   {
      if (errno != 0)
         return handleErrno(errno);

      if (log != null && log.isDebugEnabled())
         log.debug("  returning: " + v1);

      return errno;

   }

   private int handleException(Exception e)
   {
      int errno;

      if (e instanceof FuseException)
      {
         errno = handleErrno(((FuseException) e).getErrno().getErrno());
         if (log != null && log.isDebugEnabled())
            log.debug(e);
      }
      else if (e instanceof BufferOverflowException)
      {
         errno = handleErrno(Errno.ERANGE.getErrno());
         if (log != null && log.isDebugEnabled())
            log.debug(e);
      }
      else
      {
         errno = handleErrno(Errno.EFAULT.getErrno());
         if (log != null)
            log.error(e);
      }

      return errno;
   }

@Override
public boolean isImplemented(String methodName) {
	return FilesystemImplCheck.isImplemented(fs.getClass(), methodName);
}
}
