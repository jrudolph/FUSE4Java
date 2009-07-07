package fuse;

public class FuseAttr {
	private final long inode;
	private final int mode;
	private final int nlink;
	private final int uid;
	private final int gid;
	private final int rdev;
	private final long size;
	private final long blocks;
	private final int atime;
	private final int mtime;
	private final int ctime;
	
	public long getInode() {
		return inode;
	}
	
	public int getMode() {
		return mode;
	}
	
	public int getNlink() {
		return nlink;
	}
	public int getUid() {
		return uid;
	}
	public int getGid() {
		return gid;
	}
	public int getRdev() {
		return rdev;
	}
	public long getSize() {
		return size;
	}
	public long getBlocks() {
		return blocks;
	}
	public int getAtime() {
		return atime;
	}
	public int getMtime() {
		return mtime;
	}
	public int getCtime() {
		return ctime;
	}
	
	private FuseAttr(long inode, int mode, int nlink, int uid, int gid,
			int rdev, long size, long blocks, int atime, int mtime, int ctime) {
		super();
		this.inode = inode;
		this.mode = mode;
		this.nlink = nlink;
		this.uid = uid;
		this.gid = gid;
		this.rdev = rdev;
		this.size = size;
		this.blocks = blocks;
		this.atime = atime;
		this.mtime = mtime;
		this.ctime = ctime;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		private long inode = 0;
		private int mode = 0;
		private int nlink = 0;
		private int uid = 0;
		private int gid = 0 ;
		private int rdev = 0;
		private long size = 0;
		private long blocks = 0;
		private int atime = 0;
		private int mtime = 0;
		private int ctime = 0;
		
		private Builder() {
			
		}
		
		public FuseAttr build() {
			return new FuseAttr(inode, mode, nlink, uid, gid, rdev, size, blocks, atime, mtime, ctime);
		}
		
		public Builder inode(long inode) {
			this.inode = inode;
			return this;
		}
		
		public Builder mode(Mode mode) {
			return mode(mode.getMode());
		}
		
		public Builder mode(int mode) {
			this.mode |= mode;
			return this;
		}
		
		public Builder nlink(int nlink) {
			this.nlink = nlink;
			return this;
		}
		
		public Builder uid(int uid) {
			this.uid = uid;
			return this;
		}
		
		public Builder gid(int gid) {
			this.gid = gid;
			return this;
		}
		
		public Builder rdev(int rdev) {
			this.rdev = rdev;
			return this;
		}
		
		public Builder size(long size) {
			this.size = size;
			return this;
		}
		
		public Builder blocks(long blocks) {
			this.blocks = blocks;
			return this;
		}
		
		public Builder atime(int atime) {
			this.atime = atime;
			return this;
		}
		
		public Builder mtime(int mtime) {
			this.mtime = mtime;
			return this;
		}
		
		public Builder ctime(int ctime) {
			this.ctime = ctime;
			return this;
		}
	}
}
