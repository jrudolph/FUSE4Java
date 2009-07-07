package fuse;

public class FuseStatfs {
	private final int blockSize;
	private final int blocks;
	private final int blocksFree;
	private final int blocksAvail;
	private final int files;
	private final int filesFree;
	private final int namelen;
	
	private FuseStatfs(int blockSize, int blocks, int blocksFree,
			int blocksAvail, int files, int filesFree, int namelen) {
		super();
		this.blockSize = blockSize;
		this.blocks = blocks;
		this.blocksFree = blocksFree;
		this.blocksAvail = blocksAvail;
		this.files = files;
		this.filesFree = filesFree;
		this.namelen = namelen;
	}
	
	public int getBlockSize() {
		return blockSize;
	}
	public int getBlocks() {
		return blocks;
	}
	public int getBlocksFree() {
		return blocksFree;
	}
	public int getBlocksAvail() {
		return blocksAvail;
	}
	public int getFiles() {
		return files;
	}
	public int getFilesFree() {
		return filesFree;
	}
	public int getNamelen() {
		return namelen;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		private int blockSize;
		private int blocks;
		private int blocksFree;
		private int blocksAvail;
		private int files;
		private int filesFree;
		private int namelen;
		
		private Builder() {
		}
		
		public FuseStatfs build() {
			return new FuseStatfs(blockSize, blocks, blocksFree, blocksAvail, files, filesFree, namelen);
		}
		
		public Builder blockSize(int blockSize) {
			this.blockSize = blockSize;
			return this;
		}
		
		public Builder blocks(int blocks) {
			this.blocks = blocks;
			return this;
		}
		
		public Builder blocksFree(int blocksFree) {
			this.blocksFree = blocksFree;
			return this;
		}
		
		public Builder blocksAvail(int blocksAvail) {
			this.blocksAvail = blocksAvail;
			return this;
		}
		
		public Builder files(int files) {
			this.files = files;
			return this;
		}
		
		public Builder filesFree(int filesFree) {
			this.filesFree = filesFree;
			return this;
		}
		
		public Builder namelen(int namelen) {
			this.namelen = namelen;
			return this;
		}
	}
	
	
}
