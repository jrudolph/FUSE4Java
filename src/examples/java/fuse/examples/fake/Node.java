package fuse.examples.fake;

import java.util.HashMap;
import java.util.Map;

class Node {
	private static int nfiles = 0;

	public String getName() {
		return name;
	}

	public int getMode() {
		return mode;
	}

	private final String name;
	public void setMode(int mode) {
		this.mode = mode;
	}

	private int mode;
	private final Map<String, byte[]> xattrs = new HashMap<String, byte[]>();

	Node(String name, int mode, String... xattrs) {
		this.name = name;
		this.mode = mode;

		for (int i = 0; i < xattrs.length - 1; i += 2) {
			this.xattrs.put(xattrs[i], xattrs[i + 1].getBytes());
		}
		nfiles++;
	}

	public String toString() {
		String cn = getClass().getName();
		return cn.substring(cn.indexOf("$")) + "[ name=" + name + ", mode="
				+ Integer.toOctalString(mode) + "(OCT) ]";
	}
	
	public static int getFileCount() {
		return nfiles;
	}
	
	public Map<String, byte[]> getXAttr() {
		return xattrs;
	}
}
