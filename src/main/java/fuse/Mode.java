package fuse;

public enum Mode
{
	PERMISSION_MASK(0007777),   // mode bits mask
	   
	SUID_BIT(0004000),   // set UID bit
	SGID_BIT(0002000),   // set GID bit
	STICKY_BIT(0001000),   // sticky bit

	OWNER_MASK(0000700),   // mask for file owner permissions
	OWNER_READ(0000400),   // owner has read permission
	OWNER_WRITE(0000200),   // owner has write permission
	OWNER_EXECUTE(0000100),   // owner has execute permission

	GROUP_MASK(0000070),   // mask for group permissions
	GROUP_READ(0000040),   // group has read permission
	GROUP_WRITE(0000020),   // group has write permission
	GROUP_EXECUTE(0000010),   // group has execute permission

	OTHER_MASK(0000007),   // mask for permissions for others
	OTHER_READ(0000004),   // others have read permission
	OTHER_WRITE(0000002),   // others have write permission
	OTHER_EXECUTE(0000001),   // others have execute permission
	
	TYPE_MASK(0170000),
	TYPE_SOCKET(0140000),   // socket
	TYPE_SYMLINK(0120000),   // symbolic link
	TYPE_FILE(0100000),   // regular file
	TYPE_BLOCKDEV(0060000),   // block device
	TYPE_DIR(0040000),   // directory
	TYPE_CHARDEV(0020000),   // character device
	TYPE_FIFO(0010000);   // fifo
	
	private final int mode;
	private Mode(int mode) {
		this.mode = mode;
	}
	
   public int getMode() {
		return mode;
	}

}
