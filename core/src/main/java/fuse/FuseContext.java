package fuse;

/**
 * Java counterpart of struct fuse_context FUSE C API. Every instance is
 * filled-in with current Thread's active FUSE context which is only relevant
 * for the duration of a filesystem operation
 */
public class FuseContext {
	private int uid;
	private int gid;
	private int pid;

	public int getUid() {
		return uid;
	}

	public int getGid() {
		return gid;
	}

	public int getPid() {
		return pid;
	}

	private FuseContext() {
	}

	public static FuseContext get() {
		FuseContext fuseContext = new FuseContext();
		fuseContext.fillInFuseContext();
		return fuseContext;
	}

	private native void fillInFuseContext();
}
