package fuse.examples.fake;

class File extends Node
{
   private final byte[] content;

   public File(String name, int mode, String content, String ... xattrs)
   {
      super(name, mode, xattrs);
      this.content = content.getBytes();
   } 
   
   public byte[] getContent() {
	   return content;
   }
}
