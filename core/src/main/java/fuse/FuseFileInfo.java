package fuse;

public class FuseFileInfo {
	private final boolean directIO;
	private final boolean keepCache;
	private int flags;
	private Object fh;
	
	public FuseFileInfo(boolean directIO, boolean keepCache, int flags,
			Object fh) {
		super();
		this.directIO = directIO;
		this.keepCache = keepCache;
		this.flags = flags;
		this.fh = fh;
	}
	
	public boolean isDirectIO() {
		return directIO;
	}

	public boolean isKeepCache() {
		return keepCache;
	}

	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	public Object getFh() {
		return fh;
	}
	public void setFh(Object fh) {
		this.fh = fh;
	}
}
